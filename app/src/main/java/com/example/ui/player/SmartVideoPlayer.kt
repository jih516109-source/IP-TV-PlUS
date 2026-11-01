package com.example.ui.player

import android.view.KeyEvent as AndroidKeyEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.data.model.ChannelEntity
import com.example.data.model.PlayerSettings
import com.example.ui.theme.SmartCyanSecondary
import com.example.ui.theme.SmartLiveRed
import com.example.ui.theme.SmartPurpleBright
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun SmartVideoPlayer(
    channel: ChannelEntity,
    settings: PlayerSettings,
    isFullscreen: Boolean,
    onToggleFullscreen: () -> Unit,
    onPreviousChannel: () -> Unit,
    onNextChannel: () -> Unit,
    onToggleFavorite: () -> Unit,
    onClosePlayer: () -> Unit,
    onOpenChannelList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var reconnectCount by remember { mutableIntStateOf(0) }
    var showControls by remember { mutableStateOf(true) }
    var volume by remember { mutableFloatStateOf(settings.defaultVolume) }
    var isMuted by remember { mutableStateOf(false) }

    // Resize modes: 0 = FIT, 1 = ZOOM (fill), 2 = FILL (stretch)
    var resizeMode by remember { mutableIntStateOf(settings.resizeMode) }

    val focusRequester = remember { FocusRequester() }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    // Function to load and play stream
    fun loadStream(url: String) {
        errorMessage = null
        isBuffering = true
        try {
            val mediaItem = MediaItem.fromUri(url)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
            isPlaying = true
        } catch (e: Exception) {
            errorMessage = "Failed to open live stream: ${e.localizedMessage ?: "Unknown error"}"
            isBuffering = false
        }
    }

    // React to channel streamUrl changes
    LaunchedEffect(channel.id, channel.streamUrl) {
        reconnectCount = 0
        loadStream(channel.streamUrl)
    }

    // Auto-focus player container for D-Pad remote control
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Listener
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        isBuffering = true
                        errorMessage = null
                    }
                    Player.STATE_READY -> {
                        isBuffering = false
                        errorMessage = null
                        reconnectCount = 0
                    }
                    Player.STATE_ENDED -> {
                        isBuffering = false
                    }
                    Player.STATE_IDLE -> {}
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlayerError(error: PlaybackException) {
                isBuffering = false
                if (settings.autoReconnect && reconnectCount < settings.maxReconnectAttempts) {
                    reconnectCount++
                    errorMessage = "Reconnecting stream... (Attempt $reconnectCount/${settings.maxReconnectAttempts})"
                    coroutineScope.launch {
                        delay(2000L)
                        loadStream(channel.streamUrl)
                    }
                } else {
                    errorMessage = "Stream unavailable. Verify authorized M3U8 link or check connection."
                }
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Auto-hide controls
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(5000L)
            showControls = false
        }
    }

    // Volume update
    LaunchedEffect(volume, isMuted) {
        exoPlayer.volume = if (isMuted) 0f else volume
    }

    // Pulsing live badge animation
    val infiniteTransition = rememberInfiniteTransition(label = "playerPulse")
    val liveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "liveAlpha"
    )

    Box(
        modifier = modifier
            .background(Color.Black)
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type != KeyEventType.KeyDown) return@onKeyEvent false
                showControls = true
                when (keyEvent.key) {
                    Key.DirectionCenter, Key.Enter, Key.Spacebar, Key.MediaPlayPause -> {
                        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                        true
                    }
                    Key.DirectionUp, Key.ChannelUp, Key.PageUp -> {
                        onNextChannel()
                        true
                    }
                    Key.DirectionDown, Key.ChannelDown, Key.PageDown -> {
                        onPreviousChannel()
                        true
                    }
                    Key.DirectionLeft, Key.MediaPrevious -> {
                        onPreviousChannel()
                        true
                    }
                    Key.DirectionRight, Key.MediaNext -> {
                        onNextChannel()
                        true
                    }
                    Key.VolumeMute -> {
                        isMuted = !isMuted
                        true
                    }
                    Key.Back, Key.Escape -> {
                        if (isFullscreen) {
                            onToggleFullscreen()
                            true
                        } else {
                            onClosePlayer()
                            true
                        }
                    }
                    else -> false
                }
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
            }
            .testTag("video_player_container")
    ) {
        // Native Player View
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { playerView ->
                playerView.resizeMode = when (resizeMode) {
                    1 -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    2 -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                    else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Buffering & Loading Animation
        if (isBuffering && errorMessage == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            color = SmartCyanSecondary,
                            strokeWidth = 3.5.dp,
                            modifier = Modifier.size(56.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = null,
                            tint = SmartPurpleBright,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Connecting to ${channel.name}...",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Buffering live HLS stream",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Error & Reconnect State
        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.88f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = "Playback Error",
                        tint = SmartLiveRed,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Live Stream Offline or Inaccessible",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FilledTonalButton(
                            onClick = {
                                reconnectCount = 0
                                loadStream(channel.streamUrl)
                            },
                            modifier = Modifier.testTag("retry_stream_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Retry", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Retry Playback")
                        }

                        FilledTonalButton(
                            onClick = onNextChannel,
                            modifier = Modifier.testTag("skip_to_next_button")
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Next Channel")
                        }
                    }
                }
            }
        }

        // Overlay Controls
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.88f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.88f)
                            )
                        )
                    )
            ) {
                // TOP BAR: Logo, Name, Badges, Close
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClosePlayer,
                        modifier = Modifier.testTag("player_back_button")
                    ) {
                        Icon(
                            imageVector = if (isFullscreen) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Close,
                            contentDescription = "Close player",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Channel Logo in Player
                    if (channel.logoUrl.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E1738)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = channel.logoUrl,
                                contentDescription = "${channel.name} logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = channel.name,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // LIVE badge
                            Surface(
                                color = SmartLiveRed,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .alpha(liveAlpha)
                                            .background(Color.White)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "LIVE",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            // Resolution badge
                            Surface(
                                color = SmartPurpleBright.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = channel.resolutionBadge,
                                    color = SmartCyanSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${channel.category} • ${channel.country} • HLS Live",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }

                    // Favorite Button
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.testTag("player_favorite_toggle")
                    ) {
                        Icon(
                            imageVector = if (channel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (channel.isFavorite) SmartLiveRed else Color.White
                        )
                    }

                    // Channel List Drawer Button
                    IconButton(
                        onClick = onOpenChannelList,
                        modifier = Modifier.testTag("player_channels_drawer_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = "Channel list",
                            tint = SmartCyanSecondary
                        )
                    }
                }

                // CENTER PLAY/PAUSE & CHANNEL UP/DOWN CONTROLS
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Previous Channel / Channel Down
                    IconButton(
                        onClick = onPreviousChannel,
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .border(1.dp, SmartPurpleBright.copy(alpha = 0.4f), CircleShape)
                            .testTag("player_prev_channel")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous channel",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Channel Down Button
                    IconButton(
                        onClick = onPreviousChannel,
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .testTag("player_channel_down")
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Channel Down",
                            tint = SmartCyanSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Play/Pause Center
                    IconButton(
                        onClick = {
                            if (exoPlayer.isPlaying) {
                                exoPlayer.pause()
                            } else {
                                exoPlayer.play()
                            }
                        },
                        modifier = Modifier
                            .size(68.dp)
                            .background(
                                Brush.linearGradient(listOf(SmartPurpleBright, SmartCyanSecondary)),
                                CircleShape
                            )
                            .testTag("player_play_pause")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    // Channel Up Button
                    IconButton(
                        onClick = onNextChannel,
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .testTag("player_channel_up")
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Channel Up",
                            tint = SmartCyanSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Next Channel
                    IconButton(
                        onClick = onNextChannel,
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .border(1.dp, SmartPurpleBright.copy(alpha = 0.4f), CircleShape)
                            .testTag("player_next_channel")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next channel",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // BOTTOM CONTROLS BAR: Volume slider, Aspect ratio, Fullscreen
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Volume control & Mute toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.width(170.dp)
                        ) {
                            IconButton(
                                onClick = { isMuted = !isMuted },
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("player_mute_toggle")
                            ) {
                                Icon(
                                    imageVector = if (isMuted || volume == 0f) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                    contentDescription = if (isMuted) "Unmute" else "Mute",
                                    tint = if (isMuted) SmartLiveRed else Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Slider(
                                value = if (isMuted) 0f else volume,
                                onValueChange = {
                                    volume = it
                                    isMuted = false
                                },
                                colors = SliderDefaults.colors(
                                    thumbColor = SmartCyanSecondary,
                                    activeTrackColor = SmartCyanSecondary,
                                    inactiveTrackColor = Color.DarkGray
                                ),
                                modifier = Modifier.height(28.dp)
                            )
                        }

                        // Right actions: Aspect Ratio & Fullscreen
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Aspect Ratio Toggle: 0: Fit, 1: Fill, 2: Stretch
                            IconButton(
                                onClick = {
                                    resizeMode = (resizeMode + 1) % 3
                                },
                                modifier = Modifier.testTag("player_aspect_ratio_btn")
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.AspectRatio,
                                        contentDescription = "Aspect ratio",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = when (resizeMode) {
                                            1 -> "Fill"
                                            2 -> "Stretch"
                                            else -> "Fit"
                                        },
                                        color = SmartCyanSecondary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Fullscreen Toggle
                            IconButton(
                                onClick = onToggleFullscreen,
                                modifier = Modifier.testTag("player_fullscreen_btn")
                            ) {
                                Icon(
                                    imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                    contentDescription = if (isFullscreen) "Exit fullscreen" else "Fullscreen",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
