package com.example.sadkaquran.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sadkaquran.data.local.PrayerCalculationSettings
import com.example.sadkaquran.data.local.PrayerLocationSettings
import com.example.sadkaquran.data.models.prayer.AvailableMethods
import com.example.sadkaquran.ui.theme.IslamicTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerSettingsBottomSheet(
    locationSettings: PrayerLocationSettings,
    calculationSettings: PrayerCalculationSettings,
    onSaveLocation: (useGps: Boolean, city: String, country: String, lat: Double, lng: Double) -> Unit,
    onSaveCalculation: (method: Int, school: Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedMethod by remember(calculationSettings) { mutableIntStateOf(calculationSettings.method) }
    var selectedSchool by remember(calculationSettings) { mutableIntStateOf(calculationSettings.school) }
    var useGps by remember(locationSettings) { mutableStateOf(locationSettings.useGps) }
    var manualCity by remember(locationSettings) { mutableStateOf(locationSettings.city) }
    var manualCountry by remember(locationSettings) { mutableStateOf(locationSettings.country) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "إعدادات مواقيت الصلاة",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "إغلاق",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Calculation Method Section
            Text(
                text = "طريقة الحساب",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            AvailableMethods.list.forEach { method ->
                val isSelected = method.id == selectedMethod
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedMethod = method.id },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = method.nameAr,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = method.nameEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = IslamicTheme.colors.goldOrnament
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Asr Calculation School
            Text(
                text = "المذهب الفقهي لحساب صلاة العصر",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedSchool == 0,
                    onClick = { selectedSchool = 0 },
                    label = { Text("الجمهور (شافعي، مالكي، حنبلي)") },
                    modifier = Modifier
                        .weight(1f)
                        .sizeIn(minHeight = 48.dp)
                )
                FilterChip(
                    selected = selectedSchool == 1,
                    onClick = { selectedSchool = 1 },
                    label = { Text("المذهب الحنفي") },
                    modifier = Modifier
                        .weight(1f)
                        .sizeIn(minHeight = 48.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Location Mode
            Text(
                text = "طريقة تحديد الموقع",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (useGps) "تحديد تلقائي عبر GPS" else "تحديد يدوي للمدينة",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = useGps,
                    onCheckedChange = { useGps = it }
                )
            }

            if (!useGps) {
                Spacer(modifier = Modifier.height(8.dp))
                // Quick preset cities
                Text(
                    text = "مدن شائعة:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val presets = listOf(
                        Triple("القاهرة", "Cairo", "Egypt"),
                        Triple("الرياض", "Riyadh", "Saudi Arabia"),
                        Triple("مكة المكرمة", "Makkah", "Saudi Arabia"),
                        Triple("دبي", "Dubai", "United Arab Emirates")
                    )
                    presets.forEach { preset ->
                        AssistChip(
                            onClick = {
                                manualCity = preset.second
                                manualCountry = preset.third
                            },
                            label = { Text(preset.first) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = manualCity,
                    onValueChange = { manualCity = it },
                    label = { Text("المدينة (City)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = manualCountry,
                    onValueChange = { manualCountry = it },
                    label = { Text("الدولة (Country)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    onSaveCalculation(selectedMethod, selectedSchool)
                    onSaveLocation(
                        useGps,
                        manualCity,
                        manualCountry,
                        locationSettings.latitude,
                        locationSettings.longitude
                    )
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "حفظ الإعدادات",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
