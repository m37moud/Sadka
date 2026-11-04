package com.example.sadkaquran.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.sadkaquran.MainActivity
import com.example.sadkaquran.R
import com.example.sadkaquran.util.Constants.PRAYER_NOTIFICATION_CHANNEL_ID

class PrayerNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == PrayerAlarmScheduler.ACTION_PRAYER_ALARM) {
            val prayerNameAr =
                intent.getStringExtra(PrayerAlarmScheduler.EXTRA_PRAYER_NAME_AR) ?: "الصلاة"
            val prayerTime = intent.getStringExtra(PrayerAlarmScheduler.EXTRA_PRAYER_TIME) ?: ""

            showPrayerNotification(context, prayerNameAr, prayerTime)
        }
    }

    private fun showPrayerNotification(context: Context, prayerNameAr: String, prayerTime: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            200,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val cleanTime = prayerTime.split(" ")[0].trim()
        val title =
            if (prayerNameAr == "الشروق") "حان الآن وقت الشروق" else "حان الآن موعد صلاة $prayerNameAr"
        val message =
            if (prayerNameAr == "الشروق") "أشرقت الشمس ($cleanTime)" else "حي على الصلاة ($cleanTime)"

        val notification = NotificationCompat.Builder(context, PRAYER_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_quran_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setSound(soundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                PRAYER_NOTIFICATION_CHANNEL_ID,
                "تنبيهات مواقيت الصلاة",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات مواعيد الأذان والصلوات الخمس"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
