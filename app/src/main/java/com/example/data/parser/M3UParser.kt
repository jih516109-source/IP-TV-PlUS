package com.example.data.parser

import com.example.data.model.ChannelEntity
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.regex.Pattern

object M3UParser {

    private val TVG_NAME_PATTERN = Pattern.compile("tvg-name=\"([^\"]*)\"", Pattern.CASE_INSENSITIVE)
    private val TVG_LOGO_PATTERN = Pattern.compile("tvg-logo=\"([^\"]*)\"", Pattern.CASE_INSENSITIVE)
    private val GROUP_TITLE_PATTERN = Pattern.compile("group-title=\"([^\"]*)\"", Pattern.CASE_INSENSITIVE)
    private val TVG_ID_PATTERN = Pattern.compile("tvg-id=\"([^\"]*)\"", Pattern.CASE_INSENSITIVE)
    private val TVG_COUNTRY_PATTERN = Pattern.compile("tvg-country=\"([^\"]*)\"", Pattern.CASE_INSENSITIVE)

    suspend fun parseM3UStream(
        inputStream: InputStream,
        playlistId: Long? = null,
        batchSize: Int = 250,
        onBatchParsed: suspend (List<ChannelEntity>, countSoFar: Int) -> Unit
    ): Int {
        val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
        var currentName: String? = null
        var currentLogo = ""
        var currentGroup = ""
        var currentCountry = "Global"
        var currentTvgId = ""

        val buffer = mutableListOf<ChannelEntity>()
        var totalParsed = 0
        var order = 1

        try {
            var line: String? = reader.readLine()
            while (line != null) {
                val trimmed = line.trim()
                if (trimmed.isEmpty()) {
                    line = reader.readLine()
                    continue
                }

                if (trimmed.startsWith("#EXTINF:", ignoreCase = true)) {
                    // Extract attributes
                    currentTvgId = extractAttribute(TVG_ID_PATTERN, trimmed)
                    val tvgName = extractAttribute(TVG_NAME_PATTERN, trimmed)
                    currentLogo = extractAttribute(TVG_LOGO_PATTERN, trimmed)
                    currentGroup = extractAttribute(GROUP_TITLE_PATTERN, trimmed)
                    currentCountry = extractAttribute(TVG_COUNTRY_PATTERN, trimmed).ifBlank { "Global" }

                    // Extract title after last comma
                    val commaIndex = trimmed.lastIndexOf(',')
                    val parsedName = if (commaIndex != -1 && commaIndex < trimmed.length - 1) {
                        trimmed.substring(commaIndex + 1).trim()
                    } else ""

                    currentName = when {
                        parsedName.isNotBlank() -> parsedName
                        tvgName.isNotBlank() -> tvgName
                        currentTvgId.isNotBlank() -> currentTvgId
                        else -> "Channel $order"
                    }
                } else if (trimmed.startsWith("#EXTGRP:", ignoreCase = true)) {
                    currentGroup = trimmed.substringAfter(":").trim()
                } else if (!trimmed.startsWith("#")) {
                    // This is the stream URL
                    if (isValidStreamUrl(trimmed) && currentName != null) {
                        val category = categorize(currentGroup, currentName)
                        val resolution = detectResolution(currentName)
                        val channel = ChannelEntity(
                            id = 0,
                            name = currentName,
                            category = category,
                            streamUrl = trimmed,
                            logoUrl = currentLogo,
                            description = if (currentGroup.isNotBlank()) "Category: $currentGroup" else "Live broadcast stream",
                            resolutionBadge = resolution,
                            isFavorite = false,
                            isCustom = true,
                            viewerCount = 1000 + (order * 37) % 45000,
                            country = currentCountry,
                            orderIndex = order++,
                            playlistId = playlistId
                        )
                        buffer.add(channel)
                        totalParsed++

                        if (buffer.size >= batchSize) {
                            onBatchParsed(buffer.toList(), totalParsed)
                            buffer.clear()
                        }
                    }

                    // Reset for next channel
                    currentName = null
                    currentLogo = ""
                    currentGroup = ""
                    currentCountry = "Global"
                    currentTvgId = ""
                }

                line = reader.readLine()
            }

            if (buffer.isNotEmpty()) {
                onBatchParsed(buffer.toList(), totalParsed)
                buffer.clear()
            }
        } finally {
            reader.close()
        }

        return totalParsed
    }

    private fun extractAttribute(pattern: Pattern, line: String): String {
        val matcher = pattern.matcher(line)
        return if (matcher.find()) {
            matcher.group(1)?.trim() ?: ""
        } else ""
    }

    private fun isValidStreamUrl(url: String): Boolean {
        val lower = url.lowercase()
        return lower.startsWith("http://") ||
                lower.startsWith("https://") ||
                lower.startsWith("rtmp://") ||
                lower.startsWith("rtmps://") ||
                lower.startsWith("mms://") ||
                lower.endsWith(".m3u8") ||
                lower.endsWith(".m3u") ||
                lower.endsWith(".mp4") ||
                lower.contains(".m3u8?") ||
                lower.contains(".mp4?")
    }

    fun categorize(group: String, name: String): String {
        val text = "$group $name".lowercase()
        return when {
            text.contains("bangla") || text.contains("dhaka") || text.contains("bd") -> "Bangladesh"
            text.contains("india") || text.contains("hindi") || text.contains("tamil") || text.contains("telugu") -> "India"
            text.contains("sport") || text.contains("cricket") || text.contains("football") || text.contains("fifa") || text.contains("nba") -> "Sports"
            text.contains("news") || text.contains("khabar") || text.contains("cnn") || text.contains("bbc") -> "News"
            text.contains("movie") || text.contains("cinema") || text.contains("film") || text.contains("hbo") -> "Movies"
            text.contains("kid") || text.contains("cartoon") || text.contains("toon") || text.contains("disney") || text.contains("anime") -> "Kids"
            text.contains("music") || text.contains("song") || text.contains("mtv") || text.contains("radio") -> "Music"
            text.contains("doc") || text.contains("nat geo") || text.contains("discovery") || text.contains("science") || text.contains("wild") -> "Documentary"
            text.contains("enterta") || text.contains("drama") || text.contains("series") -> "Entertainment"
            group.isNotBlank() -> group.trim().replaceFirstChar { it.uppercase() }
            else -> "International"
        }
    }

    private fun detectResolution(title: String): String {
        val upper = title.uppercase()
        return when {
            upper.contains("4K") || upper.contains("UHD") || upper.contains("2160") -> "4K"
            upper.contains("FHD") || upper.contains("1080") -> "FHD"
            upper.contains("HD") || upper.contains("720") -> "HD"
            upper.contains("SD") || upper.contains("576") || upper.contains("480") -> "SD"
            else -> "HD"
        }
    }
}
