package com.example.sadkaquran.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sadkaquran.ui.theme.IslamicTheme
import com.example.sadkaquran.ui.theme.SadkaQuranTheme
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun QiblaCompassView(
    targetQiblaDirection: Double,
    currentDeviceHeading: Float,
    cityName: String,
    modifier: Modifier = Modifier
) {
    // Difference between target Qibla angle and current phone azimuth
    val relativeAngle = ((targetQiblaDirection - currentDeviceHeading + 360) % 360).toFloat()
    val isAligned = abs(relativeAngle) <= 3f || abs(relativeAngle - 360f) <= 3f

    val animatedRotation by animateFloatAsState(
        targetValue = -currentDeviceHeading,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "CompassRotation"
    )

    val dialBackgroundColor = MaterialTheme.colorScheme.surfaceContainer
    val primaryColor = MaterialTheme.colorScheme.primary
    val goldColor = IslamicTheme.colors.goldOrnament
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant

    val statusColor by animateColorAsState(
        targetValue = if (isAligned) primaryColor else goldColor,
        label = "StatusColor"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Top alignment status card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isAligned) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isAligned) Icons.Rounded.CheckCircle else Icons.Rounded.Explore,
                    contentDescription = null,
                    tint = if (isAligned) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isAligned) "أنت باتجاه القبلة تماماً" else "قم بتدوير الهاتف باتجاه القبلة",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isAligned) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Compass Circular Dial
        Box(
            modifier = Modifier
                .sizeIn(minWidth = 260.dp, maxWidth = 320.dp, minHeight = 260.dp, maxHeight = 320.dp)
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(dialBackgroundColor)
                .border(2.dp, if (isAligned) statusColor else outlineVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Rotating Compass Marks
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(animatedRotation)
            ) {
                val radius = size.minDimension / 2f
                val center = Offset(size.width / 2f, size.height / 2f)

                // Draw degree tick marks
                for (i in 0 until 360 step 15) {
                    val angleRad = Math.toRadians(i.toDouble() - 90)
                    val tickLength = if (i % 90 == 0) 18.dp.toPx() else if (i % 45 == 0) 12.dp.toPx() else 6.dp.toPx()
                    val strokeW = if (i % 90 == 0) 3.dp.toPx() else 1.5.dp.toPx()
                    val tickColor = if (i == 0) Color.Red else outlineVariant

                    val startX = (center.x + (radius - tickLength) * cos(angleRad)).toFloat()
                    val startY = (center.y + (radius - tickLength) * sin(angleRad)).toFloat()
                    val endX = (center.x + radius * cos(angleRad)).toFloat()
                    val endY = (center.y + radius * sin(angleRad)).toFloat()

                    drawLine(
                        color = tickColor,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = strokeW
                    )
                }

                // Target Qibla Marker on the outer rim
                val qiblaAngleRad = Math.toRadians(targetQiblaDirection - 90)
                val qiblaX = (center.x + (radius - 12.dp.toPx()) * cos(qiblaAngleRad)).toFloat()
                val qiblaY = (center.y + (radius - 12.dp.toPx()) * sin(qiblaAngleRad)).toFloat()
                drawCircle(
                    color = goldColor,
                    radius = 8.dp.toPx(),
                    center = Offset(qiblaX, qiblaY)
                )
            }

            // Fixed Center Pointer / Kaaba Needle pointing toward the relative Qibla angle
            val animatedNeedleRotation by animateFloatAsState(
                targetValue = relativeAngle,
                animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
                label = "NeedleRotation"
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(animatedNeedleRotation),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Top arrowhead pointing to Qibla
                Icon(
                    imageVector = Icons.Rounded.Navigation,
                    contentDescription = "اتجاه الكعبة",
                    tint = statusColor,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Center Pivot
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Qibla Info Details
        Text(
            text = "زاوية القبلة: ${targetQiblaDirection.toInt()}° بالنسبة للشمال",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = onSurfaceColor
        )

        Text(
            text = "الموقع: $cityName",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(name = "QiblaCompassView Light")
@Composable
private fun QiblaCompassViewLightPreview() {
    SadkaQuranTheme(darkTheme = false) {
        Surface {
            QiblaCompassView(
                targetQiblaDirection = 136.0,
                currentDeviceHeading = 45f,
                cityName = "القاهرة"
            )
        }
    }
}

@Preview(name = "QiblaCompassView Dark")
@Composable
private fun QiblaCompassViewDarkPreview() {
    SadkaQuranTheme(darkTheme = true) {
        Surface {
            QiblaCompassView(
                targetQiblaDirection = 136.0,
                currentDeviceHeading = 136f,
                cityName = "القاهرة"
            )
        }
    }
}
