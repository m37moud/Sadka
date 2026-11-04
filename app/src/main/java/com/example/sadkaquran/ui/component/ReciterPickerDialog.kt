package com.example.sadkaquran.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.sadkaquran.R
import com.example.sadkaquran.data.models.Shikh
import com.example.sadkaquran.playback.QuranAudioResolver
import com.example.sadkaquran.ui.theme.SadkaQuranTheme

private const val TAB_ALL = 0
private const val TAB_AYAH = 1
private const val TAB_SURAH = 2

@Composable
fun ReciterPickerDialog(
    shikhs: List<Shikh>,
    currentReciterId: String,
    onSelectReciter: (Shikh) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(TAB_ALL) }

    val filteredList = remember(shikhs, searchQuery, selectedTab) {
        shikhs.filter { shikh ->
            val matchesTab = when (selectedTab) {
                TAB_AYAH -> QuranAudioResolver.supportsAyahAudio(shikh.identifier) || shikh.type == "versebyverse"
                TAB_SURAH -> shikh.type == "surahbysurah"
                else -> true
            }
            if (!matchesTab) return@filter false

            if (searchQuery.isBlank()) {
                true
            } else {
                val q = searchQuery.trim().lowercase()
                shikh.name.lowercase().contains(q) ||
                        shikh.englishName.lowercase().contains(q) ||
                        shikh.identifier.lowercase().contains(q)
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .widthIn(max = 480.dp)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.reciter_picker_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.reciter_picker_search_placeholder)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedTab == TAB_ALL,
                        onClick = { selectedTab = TAB_ALL },
                        label = { Text(stringResource(R.string.reciter_tab_all)) },
                        colors = FilterChipDefaults.filterChipColors()
                    )
                    FilterChip(
                        selected = selectedTab == TAB_AYAH,
                        onClick = { selectedTab = TAB_AYAH },
                        label = { Text(stringResource(R.string.reciter_tab_ayah)) },
                        colors = FilterChipDefaults.filterChipColors()
                    )
                    FilterChip(
                        selected = selectedTab == TAB_SURAH,
                        onClick = { selectedTab = TAB_SURAH },
                        label = { Text(stringResource(R.string.reciter_tab_surah)) },
                        colors = FilterChipDefaults.filterChipColors()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredList, key = { it.identifier }) { shikh ->
                        val isSelected = shikh.identifier == currentReciterId
                        val hasAyahSupport = QuranAudioResolver.supportsAyahAudio(shikh.identifier) ||
                                shikh.type == "versebyverse"

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectReciter(shikh)
                                    onDismiss()
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = shikh.name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected)
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            else
                                                MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )

                                        // Badge
                                        if (hasAyahSupport) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MaterialTheme.colorScheme.secondaryContainer
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.reciter_badge_ayah),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        } else {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MaterialTheme.colorScheme.tertiaryContainer
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.reciter_badge_surah),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = shikh.englishName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("إغلاق (Close)")
                    }
                }
            }
        }
    }
}

@Preview(name = "Reciter Picker - Light")
@Composable
private fun ReciterPickerDialogPreview() {
    val sampleShikhs = listOf(
        Shikh(
            direction = "",
            englishName = "Mishary Rashid Alafasy",
            format = "audio",
            identifier = "ar.alafasy",
            language = "ar",
            name = "مشاري راشد العفاسي",
            type = "versebyverse"
        ),
        Shikh(
            direction = "",
            englishName = "Abdul Aziz az-Zahrani",
            format = "audio",
            identifier = "ar.abdulazizazzahrani",
            language = "ar",
            name = "عبد العزيز الزهراني",
            type = "surahbysurah"
        )
    )

    SadkaQuranTheme(darkTheme = false) {
        ReciterPickerDialog(
            shikhs = sampleShikhs,
            currentReciterId = "ar.alafasy",
            onSelectReciter = {},
            onDismiss = {}
        )
    }
}

@Preview(name = "Reciter Picker - Dark")
@Composable
private fun ReciterPickerDialogDarkPreview() {
    val sampleShikhs = listOf(
        Shikh(
            direction = "",
            englishName = "Mishary Rashid Alafasy",
            format = "audio",
            identifier = "ar.alafasy",
            language = "ar",
            name = "مشاري راشد العفاسي",
            type = "versebyverse"
        )
    )

    SadkaQuranTheme(darkTheme = true) {
        ReciterPickerDialog(
            shikhs = sampleShikhs,
            currentReciterId = "ar.alafasy",
            onSelectReciter = {},
            onDismiss = {}
        )
    }
}
