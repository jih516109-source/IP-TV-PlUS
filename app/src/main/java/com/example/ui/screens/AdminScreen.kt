package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ChannelEntity
import com.example.data.model.PlaylistEntity
import com.example.ui.theme.SmartCyanSecondary
import com.example.ui.theme.SmartLiveRed
import com.example.ui.theme.SmartPurpleBright
import com.example.ui.theme.SmartSurfaceElevated
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminScreen(
    isAuthenticated: Boolean,
    channels: List<ChannelEntity>,
    playlists: List<PlaylistEntity>,
    totalChannelCount: Int,
    onLogin: (String) -> Boolean,
    onLogout: () -> Unit,
    onAddChannel: (ChannelEntity) -> Unit,
    onUpdateChannel: (ChannelEntity) -> Unit,
    onDeleteChannel: (ChannelEntity) -> Unit,
    onImportUrl: (name: String, url: String) -> Unit,
    onImportContent: (name: String, content: String, source: String) -> Unit,
    onRefreshPlaylist: (PlaylistEntity) -> Unit,
    onDeletePlaylist: (PlaylistEntity) -> Unit,
    onResetDefaults: () -> Unit,
    onPreviewChannel: (ChannelEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isAuthenticated) {
        AdminLoginView(onLogin = onLogin, modifier = modifier)
    } else {
        AdminDashboardView(
            channels = channels,
            playlists = playlists,
            totalChannelCount = totalChannelCount,
            onLogout = onLogout,
            onAddChannel = onAddChannel,
            onUpdateChannel = onUpdateChannel,
            onDeleteChannel = onDeleteChannel,
            onImportUrl = onImportUrl,
            onImportContent = onImportContent,
            onRefreshPlaylist = onRefreshPlaylist,
            onDeletePlaylist = onDeletePlaylist,
            onResetDefaults = onResetDefaults,
            onPreviewChannel = onPreviewChannel,
            modifier = modifier
        )
    }
}

@Composable
private fun AdminLoginView(
    onLogin: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var pin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("admin_login_view"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SmartPurpleBright.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Admin lock",
                        tint = SmartCyanSecondary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Admin Panel Login",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Manage live channels, 1000+ M3U playlists, and categories. Default PIN: 1234",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        if (it.length <= 8) {
                            pin = it
                            errorMessage = null
                        }
                    },
                    label = { Text("Admin PIN (Default: 1234)") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SmartPurpleBright,
                        focusedLabelColor = SmartPurpleBright
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input")
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = SmartLiveRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (pin.isBlank()) {
                            errorMessage = "Please enter admin PIN"
                        } else {
                            val success = onLogin(pin)
                            if (!success) {
                                errorMessage = "Incorrect PIN. Default is 1234."
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SmartPurpleBright),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_login_submit_btn")
                ) {
                    Text("Unlock Admin Panel", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminDashboardView(
    channels: List<ChannelEntity>,
    playlists: List<PlaylistEntity>,
    totalChannelCount: Int,
    onLogout: () -> Unit,
    onAddChannel: (ChannelEntity) -> Unit,
    onUpdateChannel: (ChannelEntity) -> Unit,
    onDeleteChannel: (ChannelEntity) -> Unit,
    onImportUrl: (name: String, url: String) -> Unit,
    onImportContent: (name: String, content: String, source: String) -> Unit,
    onRefreshPlaylist: (PlaylistEntity) -> Unit,
    onDeletePlaylist: (PlaylistEntity) -> Unit,
    onResetDefaults: () -> Unit,
    onPreviewChannel: (ChannelEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Playlists (M3U), 1: Channels
    var showChannelDialog by remember { mutableStateOf(false) }
    var channelToEdit by remember { mutableStateOf<ChannelEntity?>(null) }
    var showImportUrlDialog by remember { mutableStateOf(false) }
    var showImportContentDialog by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var channelToDelete by remember { mutableStateOf<ChannelEntity?>(null) }
    var playlistToDelete by remember { mutableStateOf<PlaylistEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showImportUrlDialog = true },
                    containerColor = SmartCyanSecondary,
                    contentColor = Color.Black,
                    modifier = Modifier
                        .padding(bottom = 72.dp)
                        .testTag("admin_import_playlist_fab")
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = "Import M3U Playlist")
                }
            } else {
                FloatingActionButton(
                    onClick = {
                        channelToEdit = null
                        showChannelDialog = true
                    },
                    containerColor = SmartCyanSecondary,
                    contentColor = Color.Black,
                    modifier = Modifier
                        .padding(bottom = 72.dp)
                        .testTag("admin_add_channel_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Channel")
                }
            }
        },
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize().testTag("admin_dashboard_view")
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Dashboard Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = SmartCyanSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Admin Hub",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$totalChannelCount total live streams available",
                                style = MaterialTheme.typography.bodySmall,
                                color = SmartCyanSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onLogout,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("admin_logout_btn")
                    ) {
                        Text("Logout", fontSize = 12.sp)
                    }
                }
            }

            // Stats Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardStatCard(
                        title = "Total Channels",
                        value = totalChannelCount.toString(),
                        accent = SmartPurpleBright,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCard(
                        title = "M3U Playlists",
                        value = playlists.size.toString(),
                        accent = SmartCyanSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCard(
                        title = "Custom Streams",
                        value = channels.count { it.isCustom }.toString(),
                        accent = SmartLiveRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Navigation Tabs (Playlists vs Channels)
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = SmartPurpleBright,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlaylistPlay, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("M3U Playlists (${playlists.size})", fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Tv, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("All Channels (${channels.size})", fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }
            }

            if (selectedTab == 0) {
                // PLAYLIST MANAGEMENT VIEW
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showImportUrlDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SmartPurpleBright),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("import_url_btn")
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import URL", fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { showImportContentDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("import_content_btn")
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Paste M3U", fontSize = 13.sp)
                        }
                    }
                }

                // Presets Quick Load
                item {
                    Text(
                        text = "Quick Load Verified Free Playlists",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PresetPlaylistButton(
                            title = "IPTV-org Global Master Playlist (11,000+ Channels)",
                            subtitle = "index.m3u • All categories worldwide (Bangladesh, India, Sports, News, Movies)",
                            url = "https://iptv-org.github.io/iptv/index.m3u",
                            onLoad = { name, url -> onImportUrl(name, url) }
                        )
                        PresetPlaylistButton(
                            title = "Free-to-Air Global News (IPTV-org)",
                            subtitle = "BBC, DW, Al Jazeera, Sky, France 24",
                            url = "https://iptv-org.github.io/iptv/categories/news.m3u",
                            onLoad = { name, url -> onImportUrl(name, url) }
                        )
                        PresetPlaylistButton(
                            title = "Public Action Sports & Outdoors",
                            subtitle = "Red Bull, Outdoor TV, Skate, Surf",
                            url = "https://iptv-org.github.io/iptv/categories/sports.m3u",
                            onLoad = { name, url -> onImportUrl(name, url) }
                        )
                        PresetPlaylistButton(
                            title = "Open Documentaries & Science",
                            subtitle = "NASA, Nature, Discovery, Tech",
                            url = "https://iptv-org.github.io/iptv/categories/documentary.m3u",
                            onLoad = { name, url -> onImportUrl(name, url) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Installed Playlists",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (playlists.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No external M3U playlists imported yet.",
                                    color = Color.LightGray,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Click 'Import URL' or pick a preset above to load 1,000+ live streams.",
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                } else {
                    items(playlists, key = { it.id }) { playlist ->
                        PlaylistCardItem(
                            playlist = playlist,
                            onRefresh = { onRefreshPlaylist(playlist) },
                            onDelete = { playlistToDelete = playlist }
                        )
                    }
                }
            } else {
                // CHANNELS VIEW
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Channel Registry (${channels.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        TextButton(onClick = { showResetConfirm = true }) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset Defaults", fontSize = 12.sp, color = SmartCyanSecondary)
                        }
                    }
                }

                // Show channels with virtualization (LazyColumn)
                items(channels.take(300), key = { it.id }) { channel ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_channel_item_${channel.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Logo
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF221A3E)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (channel.logoUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = channel.logoUrl,
                                        contentDescription = channel.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(46.dp)
                                    )
                                } else {
                                    Icon(Icons.Default.Tv, contentDescription = null, tint = SmartCyanSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = channel.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = SmartPurpleBright.copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = channel.resolutionBadge,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SmartCyanSecondary,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${channel.category} • ${channel.country}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = channel.streamUrl,
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Actions: Preview, Edit, Delete
                            IconButton(
                                onClick = { onPreviewChannel(channel) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = SmartCyanSecondary)
                            }

                            IconButton(
                                onClick = {
                                    channelToEdit = channel
                                    showChannelDialog = true
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.LightGray)
                            }

                            IconButton(
                                onClick = { channelToDelete = channel },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SmartLiveRed)
                            }
                        }
                    }
                }

                if (channels.size > 300) {
                    item {
                        Text(
                            text = "... and ${channels.size - 300} more channels available in Live TV search & category views.",
                            fontSize = 12.sp,
                            color = SmartCyanSecondary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }

    // Import URL Dialog
    if (showImportUrlDialog) {
        var playlistName by remember { mutableStateOf("IPTV-org Global") }
        var playlistUrl by remember { mutableStateOf("https://iptv-org.github.io/iptv/index.m3u") }
        var inputError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showImportUrlDialog = false },
            title = { Text("Import M3U / M3U8 Playlist URL") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Enter a direct URL to an M3U, M3U8 or IPTV stream playlist. EXTINF channels, logos, categories and stream links will be parsed automatically.",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                    OutlinedTextField(
                        value = playlistName,
                        onValueChange = { playlistName = it },
                        label = { Text("Playlist Name") },
                        placeholder = { Text("e.g. Global Live TV") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("playlist_name_input")
                    )
                    OutlinedTextField(
                        value = playlistUrl,
                        onValueChange = { playlistUrl = it },
                        label = { Text("Playlist URL (M3U / M3U8)") },
                        placeholder = { Text("https://example.com/playlist.m3u8") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("playlist_url_input")
                    )
                    if (inputError != null) {
                        Text(text = inputError ?: "", color = SmartLiveRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (playlistUrl.isBlank()) {
                            inputError = "Please enter playlist URL"
                        } else {
                            onImportUrl(playlistName.ifBlank { "M3U Playlist" }, playlistUrl.trim())
                            showImportUrlDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SmartPurpleBright)
                ) {
                    Text("Start Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportUrlDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Import Content / Paste Dialog
    if (showImportContentDialog) {
        var playlistName by remember { mutableStateOf("") }
        var m3uContent by remember { mutableStateOf("") }
        var inputError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showImportContentDialog = false },
            title = { Text("Paste M3U Playlist Content") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = playlistName,
                        onValueChange = { playlistName = it },
                        label = { Text("Playlist Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = m3uContent,
                        onValueChange = { m3uContent = it },
                        label = { Text("M3U / EXTINF Content") },
                        placeholder = { Text("#EXTM3U\n#EXTINF:-1 tvg-logo=\"...\" group-title=\"News\",My Channel\nhttp://...") },
                        maxLines = 6,
                        modifier = Modifier.fillMaxWidth().height(160.dp)
                    )
                    if (inputError != null) {
                        Text(text = inputError ?: "", color = SmartLiveRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (m3uContent.isBlank()) {
                            inputError = "Please paste playlist content"
                        } else {
                            onImportContent(playlistName.ifBlank { "Pasted M3U" }, m3uContent, "Pasted Text")
                            showImportContentDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SmartPurpleBright)
                ) {
                    Text("Import Content")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportContentDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Playlist Confirmation
    if (playlistToDelete != null) {
        AlertDialog(
            onDismissRequest = { playlistToDelete = null },
            title = { Text("Delete Playlist") },
            text = { Text("Are you sure you want to delete \"${playlistToDelete?.name}\" and its ${playlistToDelete?.channelCount} channels?") },
            confirmButton = {
                Button(
                    onClick = {
                        playlistToDelete?.let { onDeletePlaylist(it) }
                        playlistToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SmartLiveRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { playlistToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add / Edit Channel Dialog
    if (showChannelDialog) {
        ChannelFormDialog(
            channel = channelToEdit,
            onDismiss = { showChannelDialog = false },
            onSave = { updatedChannel ->
                if (channelToEdit == null) {
                    onAddChannel(updatedChannel)
                } else {
                    onUpdateChannel(updatedChannel)
                }
                showChannelDialog = false
            }
        )
    }

    // Confirm Delete Dialog
    if (channelToDelete != null) {
        AlertDialog(
            onDismissRequest = { channelToDelete = null },
            title = { Text("Delete Channel") },
            text = { Text("Are you sure you want to delete \"${channelToDelete?.name}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        channelToDelete?.let { onDeleteChannel(it) }
                        channelToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SmartLiveRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { channelToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Confirm Reset Dialog
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset Channels to Defaults") },
            text = { Text("This will restore all original live sample channels (Bangladesh, India, International, Sports, etc.). Any custom channels will be reset.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetDefaults()
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SmartCyanSecondary, contentColor = Color.Black)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun PlaylistCardItem(
    playlist: PlaylistEntity,
    onRefresh: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().testTag("playlist_item_${playlist.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SmartPurpleBright.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlaylistPlay,
                    contentDescription = null,
                    tint = SmartCyanSecondary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = SmartCyanSecondary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${playlist.channelCount} channels",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SmartCyanSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Updated: ${formatTimestamp(playlist.lastUpdated)}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = playlist.sourceUrl,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Refresh & Delete Actions
            IconButton(onClick = onRefresh, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = SmartCyanSecondary)
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SmartLiveRed)
            }
        }
    }
}

@Composable
private fun PresetPlaylistButton(
    title: String,
    subtitle: String,
    url: String,
    onLoad: (String, String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SmartSurfaceElevated,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onLoad(title, url) }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = subtitle, fontSize = 11.sp, color = Color.LightGray)
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = SmartPurpleBright
            ) {
                Text(
                    text = "Load",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

private fun formatTimestamp(time: Long): String {
    return SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(time))
}

@Composable
private fun DashboardStatCard(
    title: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SmartSurfaceElevated),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 22.sp, color = accent, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChannelFormDialog(
    channel: ChannelEntity?,
    onDismiss: () -> Unit,
    onSave: (ChannelEntity) -> Unit
) {
    var name by remember { mutableStateOf(channel?.name ?: "") }
    var category by remember { mutableStateOf(channel?.category ?: "Entertainment") }
    var streamUrl by remember { mutableStateOf(channel?.streamUrl ?: "") }
    var logoUrl by remember { mutableStateOf(channel?.logoUrl ?: "") }
    var description by remember { mutableStateOf(channel?.description ?: "") }
    var resolutionBadge by remember { mutableStateOf(channel?.resolutionBadge ?: "HD") }
    var country by remember { mutableStateOf(channel?.country ?: "Global") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf("Bangladesh", "India", "International", "Sports", "News", "Movies", "Entertainment", "Kids", "Music", "Documentary")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (channel == null) "Add New Live Channel" else "Edit Channel",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Channel Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("channel_form_name")
                    )
                }

                // Category Dropdown
                item {
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .testTag("channel_form_category")
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = streamUrl,
                        onValueChange = { streamUrl = it },
                        label = { Text("Streaming URL (M3U8 / MP4) *") },
                        placeholder = { Text("https://example.com/live/master.m3u8") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("channel_form_url")
                    )
                }

                item {
                    OutlinedTextField(
                        value = logoUrl,
                        onValueChange = { logoUrl = it },
                        label = { Text("Channel Logo URL (Optional)") },
                        placeholder = { Text("https://...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("channel_form_logo")
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = resolutionBadge,
                            onValueChange = { resolutionBadge = it },
                            label = { Text("Badge (HD/FHD/4K)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = country,
                            onValueChange = { country = it },
                            label = { Text("Country") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (errorMessage != null) {
                    item {
                        Text(
                            text = errorMessage ?: "",
                            color = SmartLiveRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Channel name is required"
                    } else if (streamUrl.isBlank()) {
                        errorMessage = "Streaming URL is required"
                    } else {
                        val newEntity = ChannelEntity(
                            id = channel?.id ?: 0,
                            name = name.trim(),
                            category = category.trim(),
                            streamUrl = streamUrl.trim(),
                            logoUrl = logoUrl.trim(),
                            description = description.trim(),
                            resolutionBadge = resolutionBadge.trim().ifBlank { "HD" },
                            country = country.trim().ifBlank { "Global" },
                            isCustom = true,
                            isFavorite = channel?.isFavorite ?: false,
                            viewerCount = channel?.viewerCount ?: 1500,
                            isFeatured = channel?.isFeatured ?: false
                        )
                        onSave(newEntity)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SmartPurpleBright)
            ) {
                Text("Save Channel")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
