package com.example.sadkaquran.ui.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sadkaquran.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    // Avoid instantiating the Hilt ViewModel during Compose Preview mode to prevent NoSuchMethodException
    viewModel: SplashViewModel? = if (LocalInspectionMode.current) null else hiltViewModel()
) {
    val logoScale = remember { Animatable(0.1f) }
    val logoAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val verseAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
            )
        }
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
            )
        }
        launch {
            delay(250L.milliseconds)
            titleAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
            )
        }
        launch {
            delay(500L.milliseconds)
            verseAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
            )
        }

        delay(2000L.milliseconds)
        onSplashFinished()
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF04211D),
            Color(0xFF0A3D2F),
            Color(0xFF062F29)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Illuminated Quran Logo Container
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .graphicsLayer {
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                        alpha = logoAlpha.value
                    }
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x33FBE1A6),
                                Color(0x1A136A4C),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFFDEC58D).copy(alpha = 0.7f),
                                Color(0xFFFBE1A6).copy(alpha = 0.2f)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_reading_quran),
                    contentDescription = stringResource(id = R.string.app_name),
                    tint = Color(0xFFFBE1A6),
                    modifier = Modifier.size(76.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Title
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 32.sp
                ),
                modifier = Modifier.graphicsLayer {
                    alpha = titleAlpha.value
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Noble Quranic Ayah
            Text(
                text = "« وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا »",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFFFBE1A6),
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .graphicsLayer {
                        alpha = verseAlpha.value
                    }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Ayah Reference
            Text(
                text = "سورة المزمل: 4",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFDEC58D).copy(alpha = 0.8f),
                    fontSize = 13.sp
                ),
                modifier = Modifier.graphicsLayer {
                    alpha = verseAlpha.value
                }
            )
        }

        // Dedication Footer
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
                .graphicsLayer {
                    alpha = verseAlpha.value
                }
        ) {
            Text(
                text = "صدقة جارية عن جميع موتى المسلمين",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    MaterialTheme() {

        SplashScreen(
            onSplashFinished = {}

        )
    }

}
