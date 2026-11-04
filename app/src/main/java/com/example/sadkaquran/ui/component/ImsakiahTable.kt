package com.example.sadkaquran.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sadkaquran.data.models.prayer.PrayerDayData
import com.example.sadkaquran.ui.theme.IslamicTheme
import com.example.sadkaquran.ui.theme.SadkaQuranTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ImsakiahTable(
    days: List<PrayerDayData>,
    modifier: Modifier = Modifier
) {
    val todayFormatted = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date())

    val monthTitle = if (days.isNotEmpty()) {
        val first = days.first().date.hijri
        "${first.month.ar} ${first.year} هـ"
    } else ""

    Column(modifier = modifier.fillMaxSize()) {
        if (monthTitle.isNotBlank()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "إمساكية شهر $monthTitle",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = IslamicTheme.colors.goldOrnament
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
        }

        // Table Header Row
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableHeaderCell("اليوم", Modifier.weight(1.3f))
                TableHeaderCell("الفجر", Modifier.weight(1f))
                TableHeaderCell("الظهر", Modifier.weight(1f))
                TableHeaderCell("العصر", Modifier.weight(1f))
                TableHeaderCell("المغرب", Modifier.weight(1f))
                TableHeaderCell("العشاء", Modifier.weight(1f))
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Day Rows
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(days) { dayData ->
                val isToday = dayData.date.gregorian.date == todayFormatted
                val rowBackground = if (isToday) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }
                val contentColor = if (isToday) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }

                Surface(
                    color = rowBackground,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Day Cell
                        Column(
                            modifier = Modifier.weight(1.3f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${dayData.date.hijri.day} ${dayData.date.hijri.weekday.ar}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = contentColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Timings Cells
                        TableDataCell(
                            dayData.timings.fajr,
                            contentColor,
                            isToday,
                            Modifier.weight(1f)
                        )
                        TableDataCell(
                            dayData.timings.dhuhr,
                            contentColor,
                            isToday,
                            Modifier.weight(1f)
                        )
                        TableDataCell(
                            dayData.timings.asr,
                            contentColor,
                            isToday,
                            Modifier.weight(1f)
                        )
                        TableDataCell(
                            dayData.timings.maghrib,
                            contentColor,
                            isToday,
                            Modifier.weight(1f)
                        )
                        TableDataCell(
                            dayData.timings.isha,
                            contentColor,
                            isToday,
                            Modifier.weight(1f)
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
private fun TableHeaderCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Composable
private fun TableDataCell(
    timeString: String,
    contentColor: androidx.compose.ui.graphics.Color,
    isBold: Boolean,
    modifier: Modifier = Modifier
) {
    val clean = timeString.split(" ")[0].trim()
    Text(
        text = clean,
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp
        ),
        color = contentColor,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Preview(name = "ImsakiahTable Light")
@Composable
private fun ImsakiahTableLightPreview() {
    SadkaQuranTheme(darkTheme = false) {
        Surface {
            ImsakiahTable(days = emptyList())
        }
    }
}
