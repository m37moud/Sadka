package com.example.sadkaquran.ui.component

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sadkaquran.data.models.AyahX
import com.example.sadkaquran.util.QuranTextFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyahActionBottomSheet(
    ayah: AyahX,
    surahName: String,
    surahNumber: Int,
    isBookmarked: Boolean,
    onPlayAyah: () -> Unit,
    onToggleBookmark: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "$surahName - آية ${ayah.numberInSurah}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action: Play Ayah
            AyahActionRow(
                icon = Icons.Default.PlayCircle,
                title = "الاستماع إلى الآية",
                subtitle = "تشغيل تلاوة هذه الآية فقط",
                onClick = {
                    onPlayAyah()
                    onDismiss()
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            // Action: Bookmark
            AyahActionRow(
                icon = if (isBookmarked) Icons.Default.BookmarkAdded else Icons.Default.BookmarkBorder,
                title = if (isBookmarked) "إزالة الإشارة المرجعية" else "حفظ كإشارة مرجعية (Bookmark)",
                subtitle = "حفظ موضع الآية للرجوع إليها لاحقاً",
                onClick = {
                    onToggleBookmark()
                    val msg = if (isBookmarked) "تمت إزالة العلامة" else "تمت إضافة العلامة المرجعية"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            // Action: Copy Text
            AyahActionRow(
                icon = Icons.Default.ContentCopy,
                title = "نسخ الآية (Copy)",
                subtitle = "نسخ نص الآية مع الإسناد للحافظة",
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val formattedShareText = QuranTextFormatter.formatAyahForShare(
                        surahNumber = surahNumber,
                        ayahNumberInSurah = ayah.numberInSurah,
                        surahName = surahName,
                        rawText = ayah.text
                    )
                    val clip = ClipData.newPlainText("Quran Ayah", formattedShareText)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "تم نسخ الآية للحافظة", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            // Action: Share Text
            AyahActionRow(
                icon = Icons.Default.Share,
                title = "مشاركة الآية (Share)",
                subtitle = "مشاركة نص الآية عبر التطبيقات",
                onClick = {
                    val shareText = QuranTextFormatter.formatAyahForShare(
                        surahNumber = surahNumber,
                        ayahNumberInSurah = ayah.numberInSurah,
                        surahName = surahName,
                        rawText = ayah.text
                    ) + "\n- تطبيق صدقة جارية"
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "مشاركة الآية الكريمة"))
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AyahActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
