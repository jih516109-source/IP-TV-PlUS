package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChannelEntity
import com.example.data.model.ImportProgress
import com.example.data.model.PlayerSettings
import com.example.data.model.PlaylistEntity
import com.example.data.repository.ChannelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ChannelRepository(application)

    val allChannels: StateFlow<List<ChannelEntity>> = repository.getAllChannels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalChannelCount: StateFlow<Int> = repository.getTotalChannelCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val distinctCategories: StateFlow<List<String>> = repository.getDistinctCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playlists: StateFlow<List<PlaylistEntity>> = repository.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredChannels: StateFlow<List<ChannelEntity>> = repository.getFeaturedChannels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteChannels: StateFlow<List<ChannelEntity>> = repository.getFavoriteChannels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyWatched: StateFlow<List<ChannelEntity>> = repository.getRecentlyWatched()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lastWatchedChannel: StateFlow<ChannelEntity?> = combine(
        recentlyWatched,
        allChannels
    ) { recent, channels ->
        val lastId = repository.getLastWatchedId()
        if (lastId != -1L) {
            channels.find { it.id == lastId } ?: recent.firstOrNull()
        } else {
            recent.firstOrNull()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val playerSettings: StateFlow<PlayerSettings> = repository.playerSettings
    val themeMode: StateFlow<String> = repository.themeMode

    // Active Navigation Screen
    // 0: HOME, 1: LIVE TV, 2: FAVORITES, 3: ADMIN, 4: SETTINGS
    private val _currentScreen = MutableStateFlow(0)
    val currentScreen: StateFlow<Int> = _currentScreen.asStateFlow()

    // Currently Playing Channel
    private val _activeChannel = MutableStateFlow<ChannelEntity?>(null)
    val activeChannel: StateFlow<ChannelEntity?> = _activeChannel.asStateFlow()

    // Fullscreen state
    private val _isFullscreen = MutableStateFlow(false)
    val isFullscreen: StateFlow<Boolean> = _isFullscreen.asStateFlow()

    // Quick channel switcher drawer
    private val _showChannelDrawer = MutableStateFlow(false)
    val showChannelDrawer: StateFlow<Boolean> = _showChannelDrawer.asStateFlow()

    // Selected Category in Live TV Tab
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Admin Auth State
    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    // Import progress state
    private val _importProgress = MutableStateFlow(ImportProgress())
    val importProgress: StateFlow<ImportProgress> = _importProgress.asStateFlow()

    // Filtered channels based on category and search query
    val filteredChannels: StateFlow<List<ChannelEntity>> = combine(
        allChannels,
        _selectedCategory,
        _searchQuery
    ) { channels, category, query ->
        val trimmedQuery = query.trim()
        val queryLower = trimmedQuery.lowercase()
        val filterByCategory = category != "All" && category.isNotBlank()

        channels.filter { channel ->
            val matchesCategory = !filterByCategory || channel.category.equals(category, ignoreCase = true)
            if (!matchesCategory) return@filter false

            if (trimmedQuery.isEmpty()) return@filter true

            channel.name.contains(queryLower, ignoreCase = true) ||
                    channel.category.contains(queryLower, ignoreCase = true) ||
                    channel.country.contains(queryLower, ignoreCase = true) ||
                    channel.description.contains(queryLower, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setScreen(screenIndex: Int) {
        _currentScreen.value = screenIndex
    }

    fun playChannel(channel: ChannelEntity) {
        _activeChannel.value = channel
        viewModelScope.launch {
            repository.recordWatch(channel.id)
        }
    }

    fun closePlayer() {
        _activeChannel.value = null
        _isFullscreen.value = false
    }

    fun toggleFullscreen() {
        _isFullscreen.value = !_isFullscreen.value
    }

    fun toggleChannelDrawer(show: Boolean) {
        _showChannelDrawer.value = show
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(channel: ChannelEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(channel.id, channel.isFavorite)
        }
    }

    fun playNextChannel() {
        val channels = allChannels.value
        val current = _activeChannel.value ?: return
        if (channels.isEmpty()) return
        val currentIndex = channels.indexOfFirst { it.id == current.id }
        val nextIndex = if (currentIndex != -1 && currentIndex + 1 < channels.size) currentIndex + 1 else 0
        playChannel(channels[nextIndex])
    }

    fun playPreviousChannel() {
        val channels = allChannels.value
        val current = _activeChannel.value ?: return
        if (channels.isEmpty()) return
        val currentIndex = channels.indexOfFirst { it.id == current.id }
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else channels.size - 1
        playChannel(channels[prevIndex])
    }

    // Playlist Import & Management
    fun importPlaylistFromUrl(name: String, url: String) {
        viewModelScope.launch {
            repository.importPlaylistFromUrl(name, url) { progress ->
                _importProgress.value = progress
            }
        }
    }

    fun importPlaylistFromContent(name: String, content: String, sourceName: String) {
        viewModelScope.launch {
            repository.importPlaylistFromContent(name, content, sourceName) { progress ->
                _importProgress.value = progress
            }
        }
    }

    fun refreshPlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch {
            repository.refreshPlaylist(playlist) { progress ->
                _importProgress.value = progress
            }
        }
    }

    fun deletePlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch {
            repository.deletePlaylist(playlist)
        }
    }

    fun dismissImportProgress() {
        _importProgress.value = ImportProgress()
    }

    // Admin Operations
    fun authenticateAdmin(pin: String): Boolean {
        val success = repository.checkAdminPin(pin)
        if (success) {
            _isAdminAuthenticated.value = true
        }
        return success
    }

    fun logoutAdmin() {
        _isAdminAuthenticated.value = false
    }

    fun updateAdminPin(newPin: String) {
        repository.setAdminPin(newPin)
    }

    fun addChannel(channel: ChannelEntity) {
        viewModelScope.launch {
            repository.insertChannel(channel)
        }
    }

    fun updateChannel(channel: ChannelEntity) {
        viewModelScope.launch {
            repository.updateChannel(channel)
            if (_activeChannel.value?.id == channel.id) {
                _activeChannel.value = channel
            }
        }
    }

    fun deleteChannel(channel: ChannelEntity) {
        viewModelScope.launch {
            repository.deleteChannel(channel)
            if (_activeChannel.value?.id == channel.id) {
                closePlayer()
            }
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            repository.resetToDefaultChannels()
        }
    }

    fun clearWatchHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun updatePlayerSettings(settings: PlayerSettings) {
        repository.updateSettings(settings)
    }

    fun setThemeMode(mode: String) {
        repository.setThemeMode(mode)
    }
}
