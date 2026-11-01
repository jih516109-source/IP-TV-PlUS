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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlayerSettings
import com.example.ui.theme.SmartCyanSecondary
import com.example.ui.theme.SmartLiveRed
import com.example.ui.theme.SmartPurpleBright
import com.example.ui.theme.SmartSurfaceElevated

@Composable
fun SettingsScreen(
    currentThemeMode: String,
    playerSettings: PlayerSettings,
    onThemeChange: (String) -> Unit,
    onUpdatePlayerSettings: (PlayerSettings) -> Unit,
    onUpdateAdminPin: (String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPinDialog by remember { mutableStateOf(false) }
    var showHistoryConfirm by remember { mutableStateOf(false) }
    var historyClearedNotice by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen")
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = SmartCyanSecondary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Settings & Preferences",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Customize theme, player behavior, and admin security",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Appearance / Theme Section
        item {
            SettingsCard(title = "Appearance & Theme", icon = Icons.Default.DarkMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeOptionButton(
                        title = "Dark Modern",
                        subtitle = "Default",
                        isSelected = currentThemeMode == "DARK",
                        onClick = { onThemeChange("DARK") },
                        modifier = Modifier.weight(1f)
                    )
                    ThemeOptionButton(
                        title = "AMOLED",
                        subtitle = "Pure Black",
                        isSelected = currentThemeMode == "AMOLED",
                        onClick = { onThemeChange("AMOLED") },
                        modifier = Modifier.weight(1f)
                    )
                    ThemeOptionButton(
                        title = "Light",
                        subtitle = "Clean",
                        isSelected = currentThemeMode == "LIGHT",
                        onClick = { onThemeChange("LIGHT") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Player Settings Section
        item {
            SettingsCard(title = "Video Player Settings", icon = Icons.Default.Tv) {
                // Auto Reconnect Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Auto-Reconnect Streams",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Automatically retry loading when live stream buffers or drops",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = playerSettings.autoReconnect,
                        onCheckedChange = {
                            onUpdatePlayerSettings(playerSettings.copy(autoReconnect = it))
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = SmartCyanSecondary)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Aspect Ratio Selector
                Text(
                    text = "Default Aspect Ratio",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val modes = listOf("Fit (16:9)", "Fill (Zoom)", "Stretch")
                    modes.forEachIndexed { index, name ->
                        val isSel = playerSettings.resizeMode == index
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) SmartPurpleBright else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    onUpdatePlayerSettings(playerSettings.copy(resizeMode = index))
                                }
                        ) {
                            Text(
                                text = name,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Low Latency Mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Low Latency HLS Mode",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Minimize live stream delay on fast connections",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = playerSettings.lowLatencyMode,
                        onCheckedChange = {
                            onUpdatePlayerSettings(playerSettings.copy(lowLatencyMode = it))
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = SmartCyanSecondary)
                    )
                }
            }
        }

        // Security & Data Section
        item {
            SettingsCard(title = "Security & Data", icon = Icons.Default.Lock) {
                // Change Admin PIN button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { showPinDialog = true }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Change Admin PIN",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Update the passcode for the Admin Channel Manager",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Edit PIN",
                        color = SmartCyanSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Clear Watch History
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { showHistoryConfirm = true }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Clear Watch History",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SmartLiveRed
                        )
                        Text(
                            text = if (historyClearedNotice) "History cleared successfully!" else "Remove all channels from Recently Watched",
                            fontSize = 12.sp,
                            color = if (historyClearedNotice) SmartCyanSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(Icons.Default.Delete, contentDescription = "Clear", tint = SmartLiveRed)
                }
            }
        }

        // About Application Section
        item {
            SettingsCard(title = "About Smart TV Plus", icon = Icons.Default.Info) {
                Text(
                    text = "Smart TV Plus • Android Edition",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Version 1.0.0 (Build 100)",
                    fontSize = 12.sp,
                    color = SmartCyanSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Smart TV Plus is a high-performance live TV platform built with Jetpack Compose and Jetpack Media3 ExoPlayer. It supports HLS (.m3u8) streaming, adaptive bitrate switching, responsive multi-form factor layouts, and custom channel management.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Legal Notice: Only authorized, open-source, or creative commons streaming sources are bundled by default. Users are responsible for ensuring they possess valid broadcast or subscription licenses for any custom URLs added.",
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }

    // Change PIN Dialog
    if (showPinDialog) {
        var newPin by remember { mutableStateOf("") }
        var confirmPin by remember { mutableStateOf("") }
        var pinError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Update Admin Passcode") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newPin,
                        onValueChange = {
                            if (it.length <= 8) newPin = it
                        },
                        label = { Text("New PIN (4-8 digits)") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = {
                            if (it.length <= 8) confirmPin = it
                        },
                        label = { Text("Confirm New PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (pinError != null) {
                        Text(text = pinError ?: "", color = SmartLiveRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPin.length < 4) {
                            pinError = "PIN must be at least 4 digits"
                        } else if (newPin != confirmPin) {
                            pinError = "PINs do not match"
                        } else {
                            onUpdateAdminPin(newPin)
                            showPinDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SmartPurpleBright)
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Clear History Confirmation
    if (showHistoryConfirm) {
        AlertDialog(
            onDismissRequest = { showHistoryConfirm = false },
            title = { Text("Clear Watch History") },
            text = { Text("Are you sure you want to clear your recently watched channels history?") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearHistory()
                        historyClearedNotice = true
                        showHistoryConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SmartLiveRed)
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showHistoryConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SmartPurpleBright,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun ThemeOptionButton(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) SmartPurpleBright else SmartSurfaceElevated,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFFCBD5E1)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray
            )
        }
    }
}
