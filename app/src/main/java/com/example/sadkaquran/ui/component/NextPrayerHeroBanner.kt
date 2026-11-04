package com.example.sadkaquran.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sadkaquran.data.models.prayer.DateInfo
import com.example.sadkaquran.ui.feature.prayer.NextPrayerInfo
import com.example.sadkaquran.ui.theme.IslamicTheme
import com.example.sadkaquran.ui.theme.SadkaQuranTheme

@Composable
fun NextPrayerHeroBanner(
    cityName: String,
    dateInfo: DateInfo?,
    nextPrayer: NextPrayerInfo?,
    onRefreshLocation: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Location chip + Actions (Refresh, Settings)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Location Chip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = cityName.ifBlank { "موقعي" },
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Action Buttons: Settings & Refresh
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onRefreshLocation,
                        modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "تحديث الموقع",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = "إعدادات المواقيت",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Hijri & Gregorian Dates
            if (dateInfo != null) {
                val hijri = dateInfo.hijri
                val hijriFormatted = "${hijri.day} ${hijri.month.ar} ${hijri.year} هـ"
                val gregorianFormatted = dateInfo.readable

                Text(
                    text = hijriFormatted,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = IslamicTheme.colors.goldOrnament
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "${hijri.weekday.ar} • $gregorianFormatted",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Next Prayer Countdown Block
            if (nextPrayer != null) {
                Text(
                    text = "الصلاة القادمة: ${nextPrayer.nameAr}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = nextPrayer.formattedCountdown,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 36.sp,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "موعد الأذان: ${nextPrayer.timeFormatted}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Indicator
                LinearProgressIndicator(
                    progress = { nextPrayer.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = IslamicTheme.colors.goldOrnament,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp
                )
            }
        }
    }
}

@Preview(name = "NextPrayerHeroBanner Light")
@Composable
private fun NextPrayerHeroBannerLightPreview() {
    SadkaQuranTheme(darkTheme = false) {
        Surface {
            NextPrayerHeroBanner(
                cityName = "القاهرة",
                dateInfo = null,
                nextPrayer = NextPrayerInfo(
                    nameEn = "Asr",
                    nameAr = "العصر",
                    timeFormatted = "14:48",
                    remainingMillis = 3600000L,
                    formattedCountdown = "01:00:00",
                    progress = 0.65f
                ),
                onRefreshLocation = {},
                onOpenSettings = {}
            )
        }
    }
}

@Preview(name = "NextPrayerHeroBanner Dark")
@Composable
private fun NextPrayerHeroBannerDarkPreview() {
    SadkaQuranTheme(darkTheme = true) {
        Surface {
            NextPrayerHeroBanner(
                cityName = "القاهرة",
                dateInfo = null,
                nextPrayer = NextPrayerInfo(
                    nameEn = "Asr",
                    nameAr = "العصر",
                    timeFormatted = "14:48",
                    remainingMillis = 3600000L,
                    formattedCountdown = "01:00:00",
                    progress = 0.65f
                ),
                onRefreshLocation = {},
                onOpenSettings = {}
            )
        }
    }
}
