package com.example.sadkaquran.data.models.prayer

import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName

@Immutable
data class PrayerTimings(
    @SerializedName("Fajr") val fajr: String = "",
    @SerializedName("Sunrise") val sunrise: String = "",
    @SerializedName("Dhuhr") val dhuhr: String = "",
    @SerializedName("Asr") val asr: String = "",
    @SerializedName("Sunset") val sunset: String = "",
    @SerializedName("Maghrib") val maghrib: String = "",
    @SerializedName("Isha") val isha: String = "",
    @SerializedName("Imsak") val imsak: String = "",
    @SerializedName("Midnight") val midnight: String = "",
    @SerializedName("Firstthird") val firstThird: String = "",
    @SerializedName("Lastthird") val lastThird: String = ""
)

@Immutable
data class HijriMonth(
    @SerializedName("number") val number: Int = 1,
    @SerializedName("en") val en: String = "",
    @SerializedName("ar") val ar: String = ""
)

@Immutable
data class HijriWeekday(
    @SerializedName("en") val en: String = "",
    @SerializedName("ar") val ar: String = ""
)

@Immutable
data class HijriDate(
    @SerializedName("date") val date: String = "",
    @SerializedName("format") val format: String = "",
    @SerializedName("day") val day: String = "",
    @SerializedName("weekday") val weekday: HijriWeekday = HijriWeekday(),
    @SerializedName("month") val month: HijriMonth = HijriMonth(),
    @SerializedName("year") val year: String = "",
    @SerializedName("holidays") val holidays: List<String> = emptyList()
)

@Immutable
data class GregorianMonth(
    @SerializedName("number") val number: Int = 1,
    @SerializedName("en") val en: String = ""
)

@Immutable
data class GregorianWeekday(
    @SerializedName("en") val en: String = ""
)

@Immutable
data class GregorianDate(
    @SerializedName("date") val date: String = "",
    @SerializedName("format") val format: String = "",
    @SerializedName("day") val day: String = "",
    @SerializedName("weekday") val weekday: GregorianWeekday = GregorianWeekday(),
    @SerializedName("month") val month: GregorianMonth = GregorianMonth(),
    @SerializedName("year") val year: String = ""
)

@Immutable
data class DateInfo(
    @SerializedName("readable") val readable: String = "",
    @SerializedName("timestamp") val timestamp: String = "",
    @SerializedName("hijri") val hijri: HijriDate = HijriDate(),
    @SerializedName("gregorian") val gregorian: GregorianDate = GregorianDate()
)

@Immutable
data class PrayerMeta(
    @SerializedName("latitude") val latitude: Double = 0.0,
    @SerializedName("longitude") val longitude: Double = 0.0,
    @SerializedName("timezone") val timezone: String = ""
)

@Immutable
data class PrayerDayData(
    @SerializedName("timings") val timings: PrayerTimings = PrayerTimings(),
    @SerializedName("date") val date: DateInfo = DateInfo(),
    @SerializedName("meta") val meta: PrayerMeta = PrayerMeta()
)

@Immutable
data class PrayerTimesResponse(
    @SerializedName("code") val code: Int = 200,
    @SerializedName("status") val status: String = "",
    @SerializedName("data") val data: PrayerDayData? = null
)

@Immutable
data class PrayerCalendarResponse(
    @SerializedName("code") val code: Int = 200,
    @SerializedName("status") val status: String = "",
    @SerializedName("data") val data: List<PrayerDayData> = emptyList()
)

@Immutable
data class QiblaData(
    @SerializedName("latitude") val latitude: Double = 0.0,
    @SerializedName("longitude") val longitude: Double = 0.0,
    @SerializedName("direction") val direction: Double = 0.0
)

@Immutable
data class QiblaResponse(
    @SerializedName("code") val code: Int = 200,
    @SerializedName("status") val status: String = "",
    @SerializedName("data") val data: QiblaData? = null
)

@Immutable
data class MethodItem(
    val id: Int,
    val nameEn: String,
    val nameAr: String
)

object AvailableMethods {
    val list = listOf(
        MethodItem(5, "Egyptian General Authority of Survey", "الهيئة المصرية العامة للمساحة"),
        MethodItem(4, "Umm Al-Qura University, Makkah", "جامعة أم القرى - مكة المكرمة"),
        MethodItem(3, "Muslim World League (MWL)", "رابطة العالم الإسلامي"),
        MethodItem(
            2,
            "Islamic Society of North America (ISNA)",
            "الجمعية الإسلامية لأمريكا الشمالية"
        ),
        MethodItem(1, "University of Islamic Sciences, Karachi", "جامعة العلوم الإسلامية بكراتشي"),
        MethodItem(13, "Diyanet İşleri Başkanlığı, Turkey", "رئاسة الشؤون الدينية التركية"),
        MethodItem(8, "Gulf Region", "منطقة الخليج العربي")
    )
}
