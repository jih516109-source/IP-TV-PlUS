package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ImportProgress
import com.example.ui.theme.SmartCyanSecondary
import com.example.ui.theme.SmartLiveRed
import com.example.ui.theme.SmartPurpleBright

@Composable
fun ImportProgressDialog(
    progress: ImportProgress,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!progress.isImporting) onDismiss()
        },
        title = {
            Text(
                text = when {
                    progress.error != null -> "Import Error"
                    !progress.isImporting && progress.currentCount > 0 -> "Import Successful!"
                    else -> "Importing Live Channels"
                },
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when {
                    progress.error != null -> {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = SmartLiveRed,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = progress.error,
                            color = SmartLiveRed,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    progress.isImporting -> {
                        CircularProgressIndicator(
                            color = SmartCyanSecondary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            color = SmartPurpleBright,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = progress.currentStep,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        if (progress.currentCount > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${progress.currentCount} channels parsed and saved",
                                fontSize = 12.sp,
                                color = SmartCyanSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    else -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SmartCyanSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Successfully loaded ${progress.currentCount} channels into Smart TV Plus!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (!progress.isImporting) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = SmartPurpleBright),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("dismiss_import_dialog_btn")
                ) {
                    Text(if (progress.error != null) "Close" else "Done")
                }
            }
        }
    )
}
