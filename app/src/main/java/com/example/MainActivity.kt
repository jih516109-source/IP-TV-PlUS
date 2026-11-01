package com.example

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.data.model.ChannelEntity
import com.example.ui.MainViewModel
import com.example.ui.components.ChannelDrawerSheet
import com.example.ui.components.ImportProgressDialog
import com.example.ui.components.SmartTvSplashScreen
import com.example.ui.player.SmartVideoPlayer
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveChannelsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.SmartCyanSecondary
import com.example.ui.theme.SmartLiveRed
import com.example.ui.theme.SmartPurpleBright
import com.example.ui.theme.SmartTvTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val isFullscreen by viewModel.isFullscreen.collectAsStateWithLifecycle()
            val totalChannelCount by viewModel.totalChannelCount.collectAsStateWithLifecycle()
            var showSplash by remember { mutableStateOf(true) }

            // Handle system bars and orientation in fullscreen mode
            DisposableEffect(isFullscreen) {
                val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
                if (isFullscreen) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                    windowInsetsController.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                } else {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
                }
                onDispose {
                    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            }

            SmartTvTheme(themeMode = themeMode) {
                if (showSplash) {
                    SmartTvSplashScreen(
                        totalChannels = if (totalChannelCount > 0) totalChannelCount else 11374,
                        onAnimationFinished = { showSplash = false }
                    )
                } else {
                    MainAppContent(viewModel = viewModel)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        return when (keyCode) {
            android.view.KeyEvent.KEYCODE_CHANNEL_UP, android.view.KeyEvent.KEYCODE_PAGE_UP -> {
                viewModel.playNextChannel()
                true
            }
            android.view.KeyEvent.KEYCODE_CHANNEL_DOWN, android.view.KeyEvent.KEYCODE_PAGE_DOWN -> {
                viewModel.playPreviousChannel()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val activeChannel by viewModel.activeChannel.collectAsStateWithLifecycle()
    val isFullscreen by viewModel.isFullscreen.collectAsStateWithLifecycle()
    val showChannelDrawer by viewModel.showChannelDrawer.collectAsStateWithLifecycle()
    val playerSettings by viewModel.playerSettings.collectAsStateWithLifecycle()

    val allChannels by viewModel.allChannels.collectAsStateWithLifecycle()
    val totalChannelCount by viewModel.totalChannelCount.collectAsStateWithLifecycle()
    val distinctCategories by viewModel.distinctCategories.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val importProgress by viewModel.importProgress.collectAsStateWithLifecycle()

    val featuredChannels by viewModel.featuredChannels.collectAsStateWithLifecycle()
    val favoriteChannels by viewModel.favoriteChannels.collectAsStateWithLifecycle()
    val recentlyWatched by viewModel.recentlyWatched.collectAsStateWithLifecycle()
    val lastWatchedChannel by viewModel.lastWatchedChannel.collectAsStateWithLifecycle()
    val filteredChannels by viewModel.filteredChannels.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isAdminAuthenticated by viewModel.isAdminAuthenticated.collectAsStateWithLifecycle()
    val currentThemeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 720

    // BackHandler: Handle back navigation hierarchically
    BackHandler(enabled = isFullscreen || activeChannel != null || currentScreen != 0) {
        when {
            isFullscreen -> viewModel.toggleFullscreen()
            activeChannel != null -> viewModel.closePlayer()
            currentScreen != 0 -> viewModel.setScreen(0)
        }
    }

    if (isFullscreen && activeChannel != null) {
        // True Fullscreen Mode Player
        Box(modifier = Modifier.fillMaxSize()) {
            SmartVideoPlayer(
                channel = activeChannel!!,
                settings = playerSettings,
                isFullscreen = true,
                onToggleFullscreen = { viewModel.toggleFullscreen() },
                onPreviousChannel = { viewModel.playPreviousChannel() },
                onNextChannel = { viewModel.playNextChannel() },
                onToggleFavorite = { viewModel.toggleFavorite(activeChannel!!) },
                onClosePlayer = { viewModel.toggleFullscreen() },
                onOpenChannelList = { viewModel.toggleChannelDrawer(true) },
                modifier = Modifier.fillMaxSize()
            )

            if (showChannelDrawer) {
                ChannelDrawerSheet(
                    channels = allChannels,
                    currentChannelId = activeChannel!!.id,
                    onChannelSelect = { viewModel.playChannel(it) },
                    onDismiss = { viewModel.toggleChannelDrawer(false) }
                )
            }
        }
    } else {
        // Standard App Shell Layout
        Row(modifier = Modifier.fillMaxSize()) {
            // Adaptive Navigation Rail for Tablets / Wide screens
            if (isWideScreen) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.statusBarsPadding()
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AppLogoBadge()
                    Spacer(modifier = Modifier.height(24.dp))

                    val navItems = listOf(
                        Triple(0, "Home", Icons.Default.Home),
                        Triple(1, "Live TV", Icons.Default.LiveTv),
                        Triple(2, "Favorites", Icons.Default.Favorite),
                        Triple(3, "Admin", Icons.Default.AdminPanelSettings),
                        Triple(4, "Settings", Icons.Default.Settings)
                    )

                    navItems.forEach { (index, label, icon) ->
                        NavigationRailItem(
                            selected = currentScreen == index,
                            onClick = { viewModel.setScreen(index) },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = SmartCyanSecondary,
                                indicatorColor = SmartPurpleBright
                            ),
                            modifier = Modifier.testTag("nav_rail_item_$index")
                        )
                    }
                }
            }

            // Main Screen Scaffold
            Scaffold(
                topBar = {
                    AppTopBar(
                        channelCount = totalChannelCount,
                        onLogoClick = { viewModel.setScreen(0) }
                    )
                },
                bottomBar = {
                    if (!isWideScreen) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .navigationBarsPadding()
                                .testTag("main_bottom_nav_bar")
                        ) {
                            val items = listOf(
                                Triple(0, "Home", Icons.Default.Home),
                                Triple(1, "Live TV", Icons.Default.LiveTv),
                                Triple(2, "Favorites", Icons.Default.Favorite),
                                Triple(3, "Admin", Icons.Default.AdminPanelSettings),
                                Triple(4, "Settings", Icons.Default.Settings)
                            )

                            items.forEach { (index, label, icon) ->
                                val isSelected = currentScreen == index
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { viewModel.setScreen(index) },
                                    icon = {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = label,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = label,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = SmartCyanSecondary,
                                        indicatorColor = SmartPurpleBright
                                    ),
                                    modifier = Modifier.testTag("bottom_nav_item_$index")
                                )
                            }
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Inline Player (when a channel is active)
                    AnimatedVisibility(
                        visible = activeChannel != null,
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        activeChannel?.let { channel ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .background(Color.Black)
                            ) {
                                SmartVideoPlayer(
                                    channel = channel,
                                    settings = playerSettings,
                                    isFullscreen = false,
                                    onToggleFullscreen = { viewModel.toggleFullscreen() },
                                    onPreviousChannel = { viewModel.playPreviousChannel() },
                                    onNextChannel = { viewModel.playNextChannel() },
                                    onToggleFavorite = { viewModel.toggleFavorite(channel) },
                                    onClosePlayer = { viewModel.closePlayer() },
                                    onOpenChannelList = { viewModel.toggleChannelDrawer(true) },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    // Screen Body Content
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (currentScreen) {
                            0 -> HomeScreen(
                                featuredChannels = featuredChannels,
                                recentlyWatched = recentlyWatched,
                                allChannels = allChannels,
                                distinctCategories = distinctCategories,
                                totalChannelCount = totalChannelCount,
                                searchQuery = searchQuery,
                                lastWatchedChannel = lastWatchedChannel,
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onChannelClick = { viewModel.playChannel(it) },
                                onToggleFavorite = { viewModel.toggleFavorite(it) },
                                onCategoryClick = {
                                    viewModel.selectCategory(it)
                                    viewModel.setScreen(1)
                                },
                                onSeeAllClick = {
                                    viewModel.selectCategory("All")
                                    viewModel.setScreen(1)
                                }
                            )
                            1 -> LiveChannelsScreen(
                                channels = filteredChannels,
                                distinctCategories = distinctCategories,
                                totalChannelCount = totalChannelCount,
                                selectedCategory = selectedCategory,
                                searchQuery = searchQuery,
                                onCategorySelect = { viewModel.selectCategory(it) },
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onChannelClick = { viewModel.playChannel(it) },
                                onToggleFavorite = { viewModel.toggleFavorite(it) }
                            )
                            2 -> FavoritesScreen(
                                favorites = favoriteChannels,
                                onChannelClick = { viewModel.playChannel(it) },
                                onToggleFavorite = { viewModel.toggleFavorite(it) },
                                onBrowseChannels = { viewModel.setScreen(1) }
                            )
                            3 -> AdminScreen(
                                isAuthenticated = isAdminAuthenticated,
                                channels = allChannels,
                                playlists = playlists,
                                totalChannelCount = totalChannelCount,
                                onLogin = { viewModel.authenticateAdmin(it) },
                                onLogout = { viewModel.logoutAdmin() },
                                onAddChannel = { viewModel.addChannel(it) },
                                onUpdateChannel = { viewModel.updateChannel(it) },
                                onDeleteChannel = { viewModel.deleteChannel(it) },
                                onImportUrl = { name, url -> viewModel.importPlaylistFromUrl(name, url) },
                                onImportContent = { name, content, src -> viewModel.importPlaylistFromContent(name, content, src) },
                                onRefreshPlaylist = { viewModel.refreshPlaylist(it) },
                                onDeletePlaylist = { viewModel.deletePlaylist(it) },
                                onResetDefaults = { viewModel.resetToDefaults() },
                                onPreviewChannel = { viewModel.playChannel(it) }
                            )
                            4 -> SettingsScreen(
                                currentThemeMode = currentThemeMode,
                                playerSettings = playerSettings,
                                onThemeChange = { viewModel.setThemeMode(it) },
                                onUpdatePlayerSettings = { viewModel.updatePlayerSettings(it) },
                                onUpdateAdminPin = { viewModel.updateAdminPin(it) },
                                onClearHistory = { viewModel.clearWatchHistory() }
                            )
                        }

                        // Channel Quick Drawer Overlay
                        if (showChannelDrawer && activeChannel != null) {
                            ChannelDrawerSheet(
                                channels = allChannels,
                                currentChannelId = activeChannel!!.id,
                                onChannelSelect = { viewModel.playChannel(it) },
                                onDismiss = { viewModel.toggleChannelDrawer(false) }
                            )
                        }

                        // Import Progress Modal
                        if (importProgress.isImporting || importProgress.error != null || importProgress.currentCount > 0) {
                            ImportProgressDialog(
                                progress = importProgress,
                                onDismiss = { viewModel.dismissImportProgress() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppTopBar(channelCount: Int, onLogoClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Branding Icon + Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onLogoClick() }
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(listOf(SmartPurpleBright, SmartCyanSecondary))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LiveTv,
                        contentDescription = "Smart TV Plus Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SMART TV",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SmartPurpleBright
                        ) {
                            Text(
                                text = "PLUS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "1000+ M3U & Live IPTV Player",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Total Channels Live Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.border(1.dp, SmartPurpleBright.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(SmartLiveRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$channelCount Channels",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SmartCyanSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun AppLogoBadge() {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(listOf(SmartPurpleBright, SmartCyanSecondary))
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.LiveTv,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )
    }
}
