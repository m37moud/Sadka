package com.example.sadkaquran.ui.feature.surah

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sadkaquran.data.models.AyahX
import com.example.sadkaquran.data.models.DataX
import com.example.sadkaquran.ui.component.AyahActionBottomSheet
import com.example.sadkaquran.ui.component.ErrorUI
import com.example.sadkaquran.ui.component.FontSizeDialog
import com.example.sadkaquran.ui.component.LoadingScreen
import com.example.sadkaquran.ui.theme.IslamicTheme
import com.example.sadkaquran.ui.theme.QuranTypography
import com.example.sadkaquran.ui.theme.Shapes
import com.example.sadkaquran.util.QuranTextFormatter
import com.example.sadkaquran.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahScreen(
    navController: NavController,
    shrahNumber: Int = 1,
    elShikh: String = "",
    viewModel: SurahViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = shrahNumber) {
        viewModel.getSurah(shrahNumber)
    }

    val surahState by viewModel.surah.collectAsState()
    val arabicFontSize by viewModel.arabicFontSize.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPlayingSurah by viewModel.currentSurahNumber.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()

    var showFontSizeDialog by remember { mutableStateOf(false) }
    var selectedAyahForAction by remember { mutableStateOf<AyahX?>(null) }

    val isCurrentSurahPlaying = (currentPlayingSurah == shrahNumber && isPlaying)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (surahState is Resource.Success)
                            (surahState as Resource.Success<DataX>).data.name
                        else
                            "سورة",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    // Play Surah Audio Button
                    if (surahState is Resource.Success) {
                        val surahData = (surahState as Resource.Success<DataX>).data
                        FilledTonalIconButton(
                            onClick = { viewModel.playSurahAudio(surahData) },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = if (isCurrentSurahPlaying)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.primaryContainer,
                                contentColor = if (isCurrentSurahPlaying)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = if (isCurrentSurahPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = if (isCurrentSurahPlaying) "Pause" else "Play"
                            )
                        }
                    }

                    // Font Size Adjuster Button
                    IconButton(onClick = { showFontSizeDialog = true }) {
                        Icon(
                            imageVector = Icons.Rounded.FormatSize,
                            contentDescription = "Font size",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = surahState) {
                is Resource.Loading -> {
                    LoadingScreen()
                }

                is Resource.Error -> {
                    ErrorUI(state.error)
                }

                is Resource.Success -> {
                    SurahRead(
                        surah = state.data,
                        fontSize = arabicFontSize,
                        bookmarks = bookmarks.map { "${it.surahNumber}:${it.ayahNumber}" }.toSet(),
                        onAyahClick = { ayah ->
                            selectedAyahForAction = ayah
                            viewModel.saveLastReadAyah(ayah.numberInSurah)
                        }
                    )
                }
            }
        }
    }

    // Font size dialog
    if (showFontSizeDialog) {
        FontSizeDialog(
            currentSize = arabicFontSize,
            onSizeChange = { viewModel.updateFontSize(it) },
            onDismiss = { showFontSizeDialog = false }
        )
    }

    // Ayah action sheet
    selectedAyahForAction?.let { ayah ->
        val surahData = (surahState as? Resource.Success)?.data
        val surahName = surahData?.name ?: ""
        val isBookmarked =
            bookmarks.any { it.surahNumber == shrahNumber && it.ayahNumber == ayah.numberInSurah }

        AyahActionBottomSheet(
            ayah = ayah,
            surahName = surahName,
            surahNumber = shrahNumber,
            isBookmarked = isBookmarked,
            onPlayAyah = {
                if (surahData != null) {
                    viewModel.playAyahAudio(ayah, surahData)
                }
            },
            onToggleBookmark = {
                viewModel.toggleBookmark(ayah, surahName)
            },
            onDismiss = { selectedAyahForAction = null }
        )
    }
}

@Composable
fun SurahRead(
    surah: DataX,
    fontSize: Float,
    bookmarks: Set<String>,
    onAyahClick: (AyahX) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Surah Header Banner
        item {
            SurahHeaderBanner(surah = surah)
        }

        // Basmalah (displayed for all surahs except Surah At-Tawbah, #9, and Al-Fatihah, #1)
        if (surah.number != 9 && surah.number != 1) {
            item {
                BasmalahHeader()
            }
        }

        // Ayah Items
        items(
            items = surah.ayahs,
            key = { it.number }
        ) { ayah ->
            val isBookmarked = bookmarks.contains("${surah.number}:${ayah.numberInSurah}")
            ItemAyah(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                surahNumber = surah.number,
                ayah = ayah,
                fontSize = fontSize,
                isBookmarked = isBookmarked,
                onClick = { onAyahClick(ayah) }
            )
        }
    }
}

@Composable
fun SurahHeaderBanner(surah: DataX) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = surah.name,
                style = QuranTypography.surahTitle,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${surah.englishName} • ${surah.englishNameTranslation}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                ) {
                    Text(
                        text = "${surah.revelationType} • ${surah.numberOfAyahs} Verses",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BasmalahHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = QuranTextFormatter.STANDARD_BASMALAH,
            style = QuranTypography.basmalah,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ItemAyah(
    modifier: Modifier = Modifier,
    surahNumber: Int,
    ayah: AyahX,
    fontSize: Float,
    isBookmarked: Boolean,
    onClick: () -> Unit
) {
    val formattedAyahText = remember(surahNumber, ayah.numberInSurah, ayah.text) {
        QuranTextFormatter.formatAyahDisplay(
            surahNumber = surahNumber,
            ayahNumberInSurah = ayah.numberInSurah,
            rawText = ayah.text
        )
    }
    val arabicAyahNumber = remember(ayah.numberInSurah) {
        QuranTextFormatter.toArabicIndicDigits(ayah.numberInSurah)
    }

    Card(
        modifier = modifier.clickable { onClick() },
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = BorderStroke(
            width = if (isBookmarked) 1.5.dp else 1.dp,
            color = if (isBookmarked)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Ayah Header: Ayah Number Badge + Sajda Indicator + Bookmark + Juz/Page
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${ayah.numberInSurah}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    if (ayah.sajda) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = IslamicTheme.colors.sajdahMarker.copy(alpha = 0.18f)
                        ) {
                            Text(
                                text = "سجدة",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = IslamicTheme.colors.sajdahMarker,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (isBookmarked) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Bookmarked",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = "Juz ${ayah.juz} • Page ${ayah.page}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Arabic Ayah Text with authentic Amiri Quran font, RTL direction, generous line height, and end-of-verse marker
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = "$formattedAyahText ﴿$arabicAyahNumber﴾",
                    fontSize = fontSize.sp,
                    lineHeight = (fontSize * 2.1f).sp,
                    style = QuranTypography.ayahText,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
