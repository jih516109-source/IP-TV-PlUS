package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.AppDatabase
import com.example.data.local.ChannelDao
import com.example.data.local.DefaultChannels
import com.example.data.model.ChannelEntity
import com.example.data.model.ImportProgress
import com.example.data.model.PlayerSettings
import com.example.data.model.PlaylistEntity
import com.example.data.model.WatchHistoryEntity
import com.example.data.parser.M3UParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit

class ChannelRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val channelDao: ChannelDao = db.channelDao()
    private val prefs: SharedPreferences = context.getSharedPreferences("smart_tv_prefs", Context.MODE_PRIVATE)

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private val _playerSettings = MutableStateFlow(loadSettings())
    val playerSettings: StateFlow<PlayerSettings> = _playerSettings.asStateFlow()

    private val _themeMode = MutableStateFlow(prefs.getString("theme_mode", "DARK") ?: "DARK")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (channelDao.countChannels() < 100) {
                com.example.data.local.AssetChannelLoader.loadChannelsFromAssetsIfEmpty(context, channelDao)
                if (channelDao.countChannels() == 0) {
                    channelDao.insertChannels(DefaultChannels.INITIAL_CHANNELS)
                }
            }
        }
    }

    fun getAllChannels(): Flow<List<ChannelEntity>> = channelDao.getAllChannels()

    fun getChannelsByCategory(category: String): Flow<List<ChannelEntity>> {
        return if (category == "All" || category.isEmpty()) {
            channelDao.getAllChannels()
        } else {
            channelDao.getChannelsByCategory(category)
        }
    }

    fun getDistinctCategories(): Flow<List<String>> = channelDao.getDistinctCategories()

    fun getTotalChannelCountFlow(): Flow<Int> = channelDao.getTotalChannelCountFlow()

    fun getFavoriteChannels(): Flow<List<ChannelEntity>> = channelDao.getFavoriteChannels()

    fun getFeaturedChannels(): Flow<List<ChannelEntity>> = channelDao.getFeaturedChannels()

    fun getRecentlyWatched(): Flow<List<ChannelEntity>> = channelDao.getRecentlyWatched()

    fun searchChannels(query: String): Flow<List<ChannelEntity>> = channelDao.searchChannels(query)

    suspend fun getChannelById(id: Long): ChannelEntity? = withContext(Dispatchers.IO) {
        channelDao.getChannelById(id)
    }

    fun getChannelByIdFlow(id: Long): Flow<ChannelEntity?> = channelDao.getChannelByIdFlow(id)

    suspend fun toggleFavorite(id: Long, currentFavorite: Boolean) = withContext(Dispatchers.IO) {
        channelDao.setFavorite(id, !currentFavorite)
    }

    fun getLastWatchedId(): Long {
        return prefs.getLong("last_watched_id", -1L)
    }

    private fun setLastWatchedId(id: Long) {
        prefs.edit().putLong("last_watched_id", id).apply()
    }

    suspend fun recordWatch(channelId: Long) = withContext(Dispatchers.IO) {
        setLastWatchedId(channelId)
        channelDao.insertWatchHistory(WatchHistoryEntity(channelId = channelId))
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        prefs.edit().remove("last_watched_id").apply()
        channelDao.clearWatchHistory()
    }

    suspend fun insertChannel(channel: ChannelEntity): Long = withContext(Dispatchers.IO) {
        channelDao.insertChannel(channel)
    }

    suspend fun updateChannel(channel: ChannelEntity) = withContext(Dispatchers.IO) {
        channelDao.updateChannel(channel)
    }

    suspend fun deleteChannel(channel: ChannelEntity) = withContext(Dispatchers.IO) {
        channelDao.deleteChannel(channel)
    }

    suspend fun deleteChannelById(id: Long) = withContext(Dispatchers.IO) {
        channelDao.deleteChannelById(id)
    }

    suspend fun resetToDefaultChannels() = withContext(Dispatchers.IO) {
        channelDao.deleteAllChannels()
        com.example.data.local.AssetChannelLoader.loadChannelsFromAssetsIfEmpty(context, channelDao)
        if (channelDao.countChannels() == 0) {
            channelDao.insertChannels(DefaultChannels.INITIAL_CHANNELS)
        }
    }

    // Playlists Operations
    fun getAllPlaylists(): Flow<List<PlaylistEntity>> = channelDao.getAllPlaylists()

    suspend fun importPlaylistFromUrl(
        name: String,
        url: String,
        onProgress: (ImportProgress) -> Unit
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            onProgress(ImportProgress(isImporting = true, currentStep = "Connecting to playlist URL..."))
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "SmartTVPlus/1.0 (Android; Live Stream Player)")
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP error ${response.code}: ${response.message}"))
            }

            val responseBody = response.body ?: return@withContext Result.failure(Exception("Empty playlist response body"))
            onProgress(ImportProgress(isImporting = true, currentStep = "Creating playlist in database..."))

            val playlistEntity = PlaylistEntity(
                name = name.ifBlank { "Playlist from Web" },
                sourceUrl = url,
                lastUpdated = System.currentTimeMillis()
            )
            val playlistId = channelDao.insertPlaylist(playlistEntity)

            onProgress(ImportProgress(isImporting = true, currentStep = "Parsing EXTINF channels..."))
            val totalImported = parseAndSaveStream(responseBody.byteStream(), playlistId, onProgress)

            channelDao.updatePlaylist(playlistEntity.copy(id = playlistId, channelCount = totalImported))
            onProgress(ImportProgress(isImporting = false, currentCount = totalImported, currentStep = "Import completed!"))

            Result.success(totalImported)
        } catch (e: Exception) {
            onProgress(ImportProgress(isImporting = false, error = e.localizedMessage ?: "Unknown error"))
            Result.failure(e)
        }
    }

    suspend fun importPlaylistFromContent(
        name: String,
        content: String,
        sourceName: String = "Local File",
        onProgress: (ImportProgress) -> Unit
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            onProgress(ImportProgress(isImporting = true, currentStep = "Preparing playlist..."))
            val playlistEntity = PlaylistEntity(
                name = name.ifBlank { "Imported Playlist" },
                sourceUrl = sourceName,
                lastUpdated = System.currentTimeMillis()
            )
            val playlistId = channelDao.insertPlaylist(playlistEntity)

            val inputStream = ByteArrayInputStream(content.toByteArray(Charsets.UTF_8))
            val totalImported = parseAndSaveStream(inputStream, playlistId, onProgress)

            channelDao.updatePlaylist(playlistEntity.copy(id = playlistId, channelCount = totalImported))
            onProgress(ImportProgress(isImporting = false, currentCount = totalImported, currentStep = "Import completed!"))

            Result.success(totalImported)
        } catch (e: Exception) {
            onProgress(ImportProgress(isImporting = false, error = e.localizedMessage ?: "Unknown error"))
            Result.failure(e)
        }
    }

    suspend fun refreshPlaylist(
        playlist: PlaylistEntity,
        onProgress: (ImportProgress) -> Unit
    ): Result<Int> = withContext(Dispatchers.IO) {
        if (!playlist.sourceUrl.startsWith("http://") && !playlist.sourceUrl.startsWith("https://")) {
            return@withContext Result.failure(Exception("Cannot refresh local file playlist from web"))
        }

        try {
            onProgress(ImportProgress(isImporting = true, currentStep = "Fetching updated playlist..."))
            val request = Request.Builder()
                .url(playlist.sourceUrl)
                .header("User-Agent", "SmartTVPlus/1.0 (Android; Live Stream Player)")
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP error ${response.code}"))
            }

            val body = response.body ?: return@withContext Result.failure(Exception("Empty body"))

            onProgress(ImportProgress(isImporting = true, currentStep = "Clearing old channels..."))
            channelDao.deleteChannelsByPlaylistId(playlist.id)

            onProgress(ImportProgress(isImporting = true, currentStep = "Parsing new stream list..."))
            val totalImported = parseAndSaveStream(body.byteStream(), playlist.id, onProgress)

            channelDao.updatePlaylist(playlist.copy(channelCount = totalImported, lastUpdated = System.currentTimeMillis()))
            onProgress(ImportProgress(isImporting = false, currentCount = totalImported, currentStep = "Refresh complete!"))

            Result.success(totalImported)
        } catch (e: Exception) {
            onProgress(ImportProgress(isImporting = false, error = e.localizedMessage ?: "Unknown error"))
            Result.failure(e)
        }
    }

    suspend fun deletePlaylist(playlist: PlaylistEntity) = withContext(Dispatchers.IO) {
        channelDao.deleteChannelsByPlaylistId(playlist.id)
        channelDao.deletePlaylist(playlist)
    }

    private suspend fun parseAndSaveStream(
        inputStream: InputStream,
        playlistId: Long,
        onProgress: (ImportProgress) -> Unit
    ): Int {
        return M3UParser.parseM3UStream(
            inputStream = inputStream,
            playlistId = playlistId,
            batchSize = 250
        ) { batch, countSoFar ->
            channelDao.insertChannels(batch)
            onProgress(
                ImportProgress(
                    isImporting = true,
                    currentCount = countSoFar,
                    currentStep = "Parsed and stored $countSoFar channels..."
                )
            )
        }
    }

    // Admin Auth
    fun checkAdminPin(pin: String): Boolean {
        val storedPin = prefs.getString("admin_pin", "1234") ?: "1234"
        return pin == storedPin
    }

    fun setAdminPin(newPin: String) {
        prefs.edit().putString("admin_pin", newPin).apply()
    }

    // Settings
    private fun loadSettings(): PlayerSettings {
        return PlayerSettings(
            autoReconnect = prefs.getBoolean("pref_auto_reconnect", true),
            maxReconnectAttempts = prefs.getInt("pref_max_reconnect", 5),
            resizeMode = prefs.getInt("pref_resize_mode", 0),
            amoledPureBlack = prefs.getBoolean("pref_amoled", true),
            hardwareAcceleration = prefs.getBoolean("pref_hardware_accel", true),
            lowLatencyMode = prefs.getBoolean("pref_low_latency", true),
            defaultVolume = prefs.getFloat("pref_default_volume", 1.0f)
        )
    }

    fun updateSettings(settings: PlayerSettings) {
        prefs.edit()
            .putBoolean("pref_auto_reconnect", settings.autoReconnect)
            .putInt("pref_max_reconnect", settings.maxReconnectAttempts)
            .putInt("pref_resize_mode", settings.resizeMode)
            .putBoolean("pref_amoled", settings.amoledPureBlack)
            .putBoolean("pref_hardware_accel", settings.hardwareAcceleration)
            .putBoolean("pref_low_latency", settings.lowLatencyMode)
            .putFloat("pref_default_volume", settings.defaultVolume)
            .apply()
        _playerSettings.value = settings
    }

    fun setThemeMode(mode: String) {
        prefs.edit().putString("theme_mode", mode).apply()
        _themeMode.value = mode
    }
}
