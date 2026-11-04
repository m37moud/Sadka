package com.example.sadkaquran.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sadkaquran.R
import com.example.sadkaquran.playback.QuranPlayerManager
import com.example.sadkaquran.ui.theme.QuranTypography
import com.example.sadkaquran.ui.theme.SadkaQuranTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedMiniPlayer(
    playerManager: QuranPlayerManager,
    modifier: Modifier = Modifier
) {
    val currentTitle by playerManager.currentTitle.collectAsState()
    val currentSubtitle by playerManager.currentSubtitle.collectAsState()
    val isPlaying by playerManager.isPlaying.collectAsState()
    val isBuffering by playerManager.isBuffering.collectAsState()
    val currentPositionMs by playerManager.currentPositionMs.collectAsState()
    val durationMs by playerManager.durationMs.collectAsState()
    val playbackError by playerManager.playbackError.collectAsState()
    val currentMediaId by playerManager.currentMediaId.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }

    val hasTrack = currentMediaId != null && currentTitle.isNotBlank()

    AnimatedVisibility(
        visible = hasTrack,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            MiniPlayerCard(
                title = currentTitle,
                subtitle = currentSubtitle,
                isPlaying = isPlaying,
                isBuffering = isBuffering,
                currentPositionMs = currentPositionMs,
                durationMs = durationMs,
                playbackError = playbackError,
                onTogglePlayPause = { playerManager.togglePlayPause() },
                onStop = { playerManager.stop() },
                onRetry = { playerManager.retryPlayback() },
                onClick = { showBottomSheet = true }
            )
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            FullPlayerSheetContent(
                playerManager = playerManager,
                onDismiss = { showBottomSheet = false }
            )
        }
    }
}

@Composable
fun MiniPlayerCard(
    title: String,
    subtitle: String,
    isPlaying: Boolean,
    isBuffering: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    playbackError: String?,
    onTogglePlayPause: () -> Unit,
    onStop: () -> Unit,
    onRetry: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            val progress = if (durationMs > 0) {
                (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
            } else 0f

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isBuffering) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 2.5.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    FilledIconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier.size(40.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onStop,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (playbackError != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = playbackError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    TextButton(
                        onClick = onRetry,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.retry),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FullPlayerSheetContent(
    playerManager: QuranPlayerManager,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTitle by playerManager.currentTitle.collectAsState()
    val currentSubtitle by playerManager.currentSubtitle.collectAsState()
    val currentReciterId by playerManager.currentReciterId.collectAsState()
    val availableReciters by playerManager.availableReciters.collectAsState()
    val isPlaying by playerManager.isPlaying.collectAsState()
    val isBuffering by playerManager.isBuffering.collectAsState()
    val currentPositionMs by playerManager.currentPositionMs.collectAsState()
    val durationMs by playerManager.durationMs.collectAsState()
    val playbackError by playerManager.playbackError.collectAsState()

    var showReciterDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (availableReciters.isEmpty()) {
            playerManager.loadReciters()
        }
    }

    FullPlayerSheetLayout(
        title = currentTitle,
        subtitle = currentSubtitle,
        isPlaying = isPlaying,
        isBuffering = isBuffering,
        currentPositionMs = currentPositionMs,
        durationMs = durationMs,
        playbackError = playbackError,
        onTogglePlayPause = { playerManager.togglePlayPause() },
        onSeekTo = { pos -> playerManager.seekTo(pos) },
        onSeekForward = { playerManager.seekForward() },
        onSeekBackward = { playerManager.seekBackward() },
        onRetry = { playerManager.retryPlayback() },
        onChangeReciterClick = { showReciterDialog = true },
        modifier = modifier
    )

    if (showReciterDialog) {
        ReciterPickerDialog(
            shikhs = availableReciters,
            currentReciterId = currentReciterId ?: "",
            onSelectReciter = { shikh ->
                playerManager.switchReciter(shikh)
                showReciterDialog = false
            },
            onDismiss = { showReciterDialog = false }
        )
    }
}

@Composable
fun FullPlayerSheetLayout(
    title: String,
    subtitle: String,
    isPlaying: Boolean,
    isBuffering: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    playbackError: String?,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSeekForward: () -> Unit,
    onSeekBackward: () -> Unit,
    onRetry: () -> Unit,
    onChangeReciterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isUserDraggingSlider by remember { mutableStateOf(false) }

    LaunchedEffect(currentPositionMs) {
        if (!isUserDraggingSlider && durationMs > 0) {
            sliderPosition = currentPositionMs.toFloat()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Decorative Disc / Artwork
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = title,
            style = QuranTypography.surahTitle,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Clickable Reciter Chip below Surah title
        AssistChip(
            onClick = onChangeReciterClick,
            label = {
                Text(
                    text = subtitle.ifEmpty { stringResource(R.string.reciter) },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = stringResource(R.string.change_reciter),
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                labelColor = MaterialTheme.colorScheme.onSurface
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.heightIn(min = 48.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Time slider
        Slider(
            value = sliderPosition,
            onValueChange = {
                isUserDraggingSlider = true
                sliderPosition = it
            },
            onValueChangeFinished = {
                isUserDraggingSlider = false
                onSeekTo(sliderPosition.toLong())
            },
            valueRange = 0f..durationMs.toFloat().coerceAtLeast(1f),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDuration(currentPositionMs),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatDuration(durationMs),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onSeekBackward,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Replay10,
                    contentDescription = "Rewind 10 seconds",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            if (isBuffering) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                FilledIconButton(
                    onClick = onTogglePlayPause,
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            IconButton(
                onClick = onSeekForward,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Forward10,
                    contentDescription = "Forward 10 seconds",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (playbackError != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.errorContainer,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = playbackError,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = onRetry
                ) {
                    Text(
                        text = stringResource(R.string.retry),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

@Preview(name = "MiniPlayer Light", showBackground = true)
@Composable
private fun MiniPlayerCardLightPreview() {
    SadkaQuranTheme(darkTheme = false) {
        Surface {
            MiniPlayerCard(
                title = "سورة البقرة",
                subtitle = "مشاري راشد العفاسي",
                isPlaying = true,
                isBuffering = false,
                currentPositionMs = 150000L,
                durationMs = 600000L,
                playbackError = null,
                onTogglePlayPause = {},
                onStop = {},
                onRetry = {},
                onClick = {},
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Preview(name = "MiniPlayer Dark", showBackground = true)
@Composable
private fun MiniPlayerCardDarkPreview() {
    SadkaQuranTheme(darkTheme = true) {
        Surface {
            MiniPlayerCard(
                title = "سورة البقرة",
                subtitle = "مشاري راشد العفاسي",
                isPlaying = false,
                isBuffering = false,
                currentPositionMs = 150000L,
                durationMs = 600000L,
                playbackError = null,
                onTogglePlayPause = {},
                onStop = {},
                onRetry = {},
                onClick = {},
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Preview(name = "FullPlayer Light", showBackground = true)
@Composable
private fun FullPlayerSheetLightPreview() {
    SadkaQuranTheme(darkTheme = false) {
        Surface {
            FullPlayerSheetLayout(
                title = "سورة الفاتحة",
                subtitle = "عبد الباسط عبد الصمد",
                isPlaying = true,
                isBuffering = false,
                currentPositionMs = 25000L,
                durationMs = 65000L,
                playbackError = null,
                onTogglePlayPause = {},
                onSeekTo = {},
                onSeekForward = {},
                onSeekBackward = {},
                onRetry = {},
                onChangeReciterClick = {}
            )
        }
    }
}

@Preview(name = "FullPlayer Dark", showBackground = true)
@Composable
private fun FullPlayerSheetDarkPreview() {
    SadkaQuranTheme(darkTheme = true) {
        Surface {
            FullPlayerSheetLayout(
                title = "سورة الفاتحة",
                subtitle = "عبد الباسط عبد الصمد",
                isPlaying = false,
                isBuffering = false,
                currentPositionMs = 25000L,
                durationMs = 65000L,
                playbackError = null,
                onTogglePlayPause = {},
                onSeekTo = {},
                onSeekForward = {},
                onSeekBackward = {},
                onRetry = {},
                onChangeReciterClick = {}
            )
        }
    }
}

@Preview(name = "FullPlayer Compact", widthDp = 320, fontScale = 1.3f, showBackground = true)
@Composable
private fun FullPlayerSheetCompactPreview() {
    SadkaQuranTheme(darkTheme = false) {
        Surface {
            FullPlayerSheetLayout(
                title = "سورة آل عمران",
                subtitle = "سعد الغامدي",
                isPlaying = true,
                isBuffering = false,
                currentPositionMs = 50000L,
                durationMs = 300000L,
                playbackError = null,
                onTogglePlayPause = {},
                onSeekTo = {},
                onSeekForward = {},
                onSeekBackward = {},
                onRetry = {},
                onChangeReciterClick = {}
            )
        }
    }
}
