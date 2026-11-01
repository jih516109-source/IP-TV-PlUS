package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SmartCyanSecondary
import com.example.ui.theme.SmartLiveRed
import com.example.ui.theme.SmartPurpleBright
import com.example.ui.theme.SmartPurplePrimary
import kotlinx.coroutines.delay

@Composable
fun SmartTvSplashScreen(
    totalChannels: Int = 11374,
    onAnimationFinished: () -> Unit
) {
    // Animation states
    val scale = remember { Animatable(0.4f) }
    val alpha = remember { Animatable(0f) }
    val progress = remember { Animatable(0f) }
    var statusText by remember { mutableStateOf("Initializing Live TV Engine...") }

    // Pulsing glowing ring animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulseRing")
    val ringScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringScale"
    )
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringAlpha"
    )

    LaunchedEffect(Unit) {
        // Step 1: Scale and fade in logo
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 400)
        )

        // Step 2: Progress loading simulation
        statusText = "Loading $totalChannels Channels & M3U Playlists..."
        progress.animateTo(
            targetValue = 0.65f,
            animationSpec = tween(durationMillis = 650, easing = LinearEasing)
        )

        delay(250)
        statusText = "Preparing ExoPlayer HLS Live Engine..."
        progress.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 500, easing = LinearEasing)
        )

        delay(200)
        statusText = "Ready!"
        delay(250)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E143E),
                        Color(0xFF0F0A24),
                        Color(0xFF060410)
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Tap to skip intro immediately
                onAnimationFinished()
            }
            .testTag("app_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing glow rings
        Box(
            modifier = Modifier
                .size(190.dp)
                .scale(ringScale)
                .alpha(ringAlpha)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(listOf(SmartPurpleBright, SmartCyanSecondary)),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(240.dp)
                .scale(ringScale * 1.15f)
                .alpha(ringAlpha * 0.6f)
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(listOf(SmartCyanSecondary, SmartPurplePrimary)),
                    shape = CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
                .padding(32.dp)
        ) {
            // Main Holographic Icon Box
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(SmartPurpleBright, SmartCyanSecondary, Color(0xFF3B82F6))
                        )
                    )
                    .border(
                        2.dp,
                        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.8f), SmartCyanSecondary)),
                        RoundedCornerShape(26.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LiveTv,
                    contentDescription = "Smart TV Plus",
                    tint = Color.White,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Name & LIVE Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "SMART TV",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SmartPurpleBright,
                    modifier = Modifier.border(1.dp, SmartCyanSecondary.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                ) {
                    Text(
                        text = "PLUS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = SmartLiveRed,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "M3U8 Streaming & IPTV Player",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = SmartCyanSecondary,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Animated Loading Progress Bar
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                LinearProgressIndicator(
                    progress = { progress.value },
                    color = SmartCyanSecondary,
                    trackColor = Color(0xFF281E48),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status message
            Text(
                text = statusText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFCBD5E1),
                textAlign = TextAlign.Center
            )
        }

        // Bottom Footer
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Android TV • Mobile • HLS Media3",
                fontSize = 11.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap screen to skip",
                fontSize = 10.sp,
                color = Color.DarkGray
            )
        }
    }
}
