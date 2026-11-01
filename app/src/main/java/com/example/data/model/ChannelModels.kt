package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "channels",
    indices = [
        Index(value = ["category"]),
        Index(value = ["isFavorite"]),
        Index(value = ["playlistId"]),
        Index(value = ["name"])
    ]
)
data class ChannelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val streamUrl: String,
    val logoUrl: String = "",
    val description: String = "",
    val resolutionBadge: String = "HD",
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false,
    val viewerCount: Int = 1200,
    val isFeatured: Boolean = false,
    val country: String = "Global",
    val orderIndex: Int = 0,
    val playlistId: Long? = null
)

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val sourceUrl: String, // Web URL or "File: <filename>"
    val channelCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isEnabled: Boolean = true
)

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey
    val channelId: Long,
    val watchedAt: Long = System.currentTimeMillis()
)

data class ImportProgress(
    val isImporting: Boolean = false,
    val currentCount: Int = 0,
    val totalEstimated: Int = 0,
    val currentStep: String = "",
    val error: String? = null
)

enum class ChannelCategory(val displayName: String, val iconName: String) {
    ALL("All", "Apps"),
    BANGLADESH("Bangladesh", "Flag"),
    INDIA("India", "Flag"),
    INTERNATIONAL("International", "Public"),
    SPORTS("Sports", "SportsSoccer"),
    NEWS("News", "Newspaper"),
    MOVIES("Movies", "Movie"),
    ENTERTAINMENT("Entertainment", "LiveTv"),
    KIDS("Kids", "ChildCare"),
    MUSIC("Music", "MusicNote"),
    DOCUMENTARY("Documentary", "Landscape")
}

data class PlayerSettings(
    val autoReconnect: Boolean = true,
    val maxReconnectAttempts: Int = 5,
    val resizeMode: Int = 0, // 0: Fit, 1: Fill (Crop), 2: Stretch
    val amoledPureBlack: Boolean = true,
    val hardwareAcceleration: Boolean = true,
    val lowLatencyMode: Boolean = true,
    val defaultVolume: Float = 1.0f
)
