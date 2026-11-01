package com.example.data.local

import android.content.Context
import android.util.JsonReader
import com.example.data.model.ChannelEntity
import com.example.data.model.PlaylistEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

object AssetChannelLoader {

    private const val ASSET_FILE = "iptv_org_channels.json"
    const val IPTV_ORG_URL = "https://iptv-org.github.io/iptv/index.m3u"
    const val IPTV_ORG_NAME = "IPTV-org Global Master (index.m3u)"

    suspend fun loadChannelsFromAssetsIfEmpty(context: Context, dao: ChannelDao) = withContext(Dispatchers.IO) {
        val count = dao.countChannels()
        // If database has only default demo channels or is empty, load all 11,374 channels from assets!
        if (count < 100) {
            try {
                val inputStream = context.assets.open(ASSET_FILE)
                val reader = JsonReader(InputStreamReader(inputStream, Charsets.UTF_8))

                val playlist = PlaylistEntity(
                    id = 1,
                    name = IPTV_ORG_NAME,
                    sourceUrl = IPTV_ORG_URL,
                    channelCount = 0,
                    lastUpdated = System.currentTimeMillis()
                )
                val playlistId = dao.insertPlaylist(playlist)

                val buffer = mutableListOf<ChannelEntity>()
                var totalInserted = 0

                reader.beginArray()
                while (reader.hasNext()) {
                    var name = ""
                    var category = "Entertainment"
                    var streamUrl = ""
                    var logoUrl = ""
                    var description = ""
                    var resolutionBadge = "HD"
                    var country = "Global"
                    var viewerCount = 1200
                    var orderIndex = totalInserted + 1

                    reader.beginObject()
                    while (reader.hasNext()) {
                        when (reader.nextName()) {
                            "name" -> name = reader.nextString()
                            "category" -> category = reader.nextString()
                            "streamUrl" -> streamUrl = reader.nextString()
                            "logoUrl" -> logoUrl = reader.nextString()
                            "description" -> description = reader.nextString()
                            "resolutionBadge" -> resolutionBadge = reader.nextString()
                            "country" -> country = reader.nextString()
                            "viewerCount" -> viewerCount = reader.nextInt()
                            "orderIndex" -> orderIndex = reader.nextInt()
                            else -> reader.skipValue()
                        }
                    }
                    reader.endObject()

                    if (name.isNotBlank() && streamUrl.isNotBlank()) {
                        buffer.add(
                            ChannelEntity(
                                id = 0,
                                name = name,
                                category = category,
                                streamUrl = streamUrl,
                                logoUrl = logoUrl,
                                description = description,
                                resolutionBadge = resolutionBadge,
                                country = country,
                                viewerCount = viewerCount,
                                orderIndex = orderIndex,
                                isFavorite = false,
                                isCustom = true,
                                playlistId = playlistId
                            )
                        )
                        totalInserted++

                        if (buffer.size >= 400) {
                            dao.insertChannels(buffer)
                            buffer.clear()
                        }
                    }
                }
                reader.endArray()
                reader.close()

                if (buffer.isNotEmpty()) {
                    dao.insertChannels(buffer)
                    buffer.clear()
                }

                // Update playlist with total channel count
                dao.updatePlaylist(playlist.copy(id = playlistId, channelCount = totalInserted))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
