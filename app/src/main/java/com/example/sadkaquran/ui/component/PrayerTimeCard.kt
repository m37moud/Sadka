package com.example.sadkaquran.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sadkaquran.ui.theme.IslamicTheme
import com.example.sadkaquran.ui.theme.SadkaQuranTheme

@Composable
fun PrayerTimeCard(
    nameAr: String,
    nameEn: String,
    timeString: String,
    isActive: Boolean,
    isNotificationEnabled: Boolean,
    onToggleNotification: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cleanTime = timeString.split(" ")[0].trim()

    val containerColor by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "PrayerCardContainer"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        label = "PrayerCardContent"
    )

    val borderColor = if (isActive) {
        IslamicTheme.colors.goldOrnament
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isActive) 1.5.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Right side (RTL context) / Start info: Prayer Name & Active Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isActive) {
                        IslamicTheme.colors.goldOrnament
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (nameEn.equals("Sunrise", true)) {
                                Icons.Rounded.WbSunny
                            } else {
                                Icons.Rounded.AccessTime
                            },
                            contentDescription = nameAr,
                            tint = if (isActive) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = nameAr,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold
                        ),
                        color = contentColor
                    )
                    Text(
                        text = nameEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Left side / Trailing content: Time & Notification Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = cleanTime,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = contentColor
                )

                IconButton(
                    onClick = onToggleNotification,
                    modifier = Modifier
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                ) {
                    Icon(
                        imageVector = if (isNotificationEnabled) {
                            Icons.Filled.Notifications
                        } else {
                            Icons.Outlined.NotificationsNone
                        },
                        contentDescription = "Toggle notification for $nameAr",
                        tint = if (isNotificationEnabled) {
                            IslamicTheme.colors.goldOrnament
                        } else {
                            MaterialTheme.colorScheme.outline
                        }
                    )
                }
            }
        }
    }
}

@Preview(name = "PrayerTimeCard Light")
@Composable
private fun PrayerTimeCardLightPreview() {
    SadkaQuranTheme(darkTheme = false) {
        Surface {
            Column(modifier = Modifier.padding(8.dp)) {
                PrayerTimeCard(
                    nameAr = "العصر",
                    nameEn = "Asr",
                    timeString = "14:48",
                    isActive = true,
                    isNotificationEnabled = true,
                    onToggleNotification = {}
                )
                PrayerTimeCard(
                    nameAr = "المغرب",
                    nameEn = "Maghrib",
                    timeString = "17:07",
                    isActive = false,
                    isNotificationEnabled = false,
                    onToggleNotification = {}
                )
            }
        }
    }
}

@Preview(name = "PrayerTimeCard Dark")
@Composable
private fun PrayerTimeCardDarkPreview() {
    SadkaQuranTheme(darkTheme = true) {
        Surface {
            Column(modifier = Modifier.padding(8.dp)) {
                PrayerTimeCard(
                    nameAr = "العصر",
                    nameEn = "Asr",
                    timeString = "14:48",
                    isActive = true,
                    isNotificationEnabled = true,
                    onToggleNotification = {}
                )
            }
        }
    }
}
