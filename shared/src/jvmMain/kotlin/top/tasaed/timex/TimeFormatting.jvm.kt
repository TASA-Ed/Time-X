package top.tasaed.timex

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Instant

private var localizedLocale: Locale? = null
private var localizedFormatter: DateFormat? = null
private var customPattern: String? = null
private var customFormatter: SimpleDateFormat? = null

internal actual fun formatLocalizedDateTime(instant: Instant): String {
    val locale = Locale.getDefault()
    if (localizedFormatter == null || localizedLocale != locale) {
        localizedLocale = locale
        localizedFormatter = DateFormat.getDateTimeInstance(
            DateFormat.LONG,
            DateFormat.LONG,
            locale,
        )
    }
    return localizedFormatter!!.format(Date(instant.toEpochMilliseconds()))
}

internal actual fun formatCustomDateTime(instant: Instant, pattern: String): String? {
    if (customFormatter == null || customPattern != pattern) {
        customPattern = pattern
        customFormatter = try {
            SimpleDateFormat(pattern, Locale.getDefault())
        } catch (_: IllegalArgumentException) {
            null
        }
    }
    return try {
        customFormatter?.format(Date(instant.toEpochMilliseconds()))
    } catch (_: IllegalArgumentException) {
        null
    }
}

internal actual fun isValidCustomTimeFormat(pattern: String): Boolean {
    if (pattern.isEmpty()) return false
    return try {
        SimpleDateFormat(pattern, Locale.getDefault())
        true
    } catch (_: IllegalArgumentException) {
        false
    }
}
