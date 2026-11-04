package com.example.sadkaquran.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.sadkaquran.data.local.PrayerNotificationSettings
import com.example.sadkaquran.data.models.prayer.PrayerTimings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PrayerAlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    fun schedulePrayerAlarms(
        timings: PrayerTimings,
        notificationSettings: PrayerNotificationSettings
    ) {
        if (alarmManager == null) return

        val prayers = listOf(
            PrayerAlarmItem("Fajr", "الفجر", timings.fajr, notificationSettings.fajrEnabled, 101),
            PrayerAlarmItem("Sunrise", "الشروق", timings.sunrise, notificationSettings.sunriseEnabled, 102),
            PrayerAlarmItem("Dhuhr", "الظهر", timings.dhuhr, notificationSettings.dhuhrEnabled, 103),
            PrayerAlarmItem("Asr", "العصر", timings.asr, notificationSettings.asrEnabled, 104),
            PrayerAlarmItem("Maghrib", "المغرب", timings.maghrib, notificationSettings.maghribEnabled, 105),
            PrayerAlarmItem("Isha", "العشاء", timings.isha, notificationSettings.ishaEnabled, 106)
        )

        val now = System.currentTimeMillis()

        prayers.forEach { prayer ->
            if (!prayer.isEnabled) {
                cancelAlarm(prayer.requestCode)
                return@forEach
            }

            val triggerTime = parseTodayTimeToMillis(prayer.timeString)
            if (triggerTime > now) {
                scheduleExact(triggerTime, prayer)
            }
        }
    }

    private fun scheduleExact(triggerAtMillis: Long, prayer: PrayerAlarmItem) {
        val intent = Intent(context, PrayerNotificationReceiver::class.java).apply {
            action = ACTION_PRAYER_ALARM
            putExtra(EXTRA_PRAYER_NAME_EN, prayer.nameEn)
            putExtra(EXTRA_PRAYER_NAME_AR, prayer.nameAr)
            putExtra(EXTRA_PRAYER_TIME, prayer.timeString)
        }

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getBroadcast(context, prayer.requestCode, intent, flags)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager?.canScheduleExactAlarms() == true) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager?.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager?.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Log.w("PrayerAlarmScheduler", "Exact alarm permission missing: ${e.localizedMessage}")
            alarmManager?.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    private fun cancelAlarm(requestCode: Int) {
        val intent = Intent(context, PrayerNotificationReceiver::class.java).apply {
            action = ACTION_PRAYER_ALARM
        }
        val flags = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags)
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun parseTodayTimeToMillis(timeString: String): Long {
        return try {
            val cleanTime = timeString.split(" ")[0].trim() // Remove timezone e.g. "(EET)"
            val parts = cleanTime.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()

            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        } catch (e: Exception) {
            0L
        }
    }

    data class PrayerAlarmItem(
        val nameEn: String,
        val nameAr: String,
        val timeString: String,
        val isEnabled: Boolean,
        val requestCode: Int
    )

    companion object {
        const val ACTION_PRAYER_ALARM = "com.example.sadkaquran.ACTION_PRAYER_ALARM"
        const val EXTRA_PRAYER_NAME_EN = "extra_prayer_name_en"
        const val EXTRA_PRAYER_NAME_AR = "extra_prayer_name_ar"
        const val EXTRA_PRAYER_TIME = "extra_prayer_time"
    }
}
