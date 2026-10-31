package top.tasaed.timex

import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Built-in and custom formats for the time shown on the main screen.
 *
 * The custom pattern follows the platform's standard date/time pattern syntax.
 */
enum class TimeTextFormat {
  HoursMinutesSeconds,
  LocalizedDateTime,
  UnixSeconds,
  UnixMilliseconds,
  Custom,
}

const val DefaultCustomTimeFormat = "HH:mm:ss"

internal expect fun formatLocalizedDateTime(instant: Instant): String

internal expect fun formatCustomDateTime(instant: Instant, pattern: String): String?

internal expect fun isValidCustomTimeFormat(pattern: String): Boolean

internal fun formatTimeText(
  instant: Instant,
  format: TimeTextFormat,
  customPattern: String,
): String =
  when (format) {
    TimeTextFormat.HoursMinutesSeconds -> {
      val time = instant.toLocalDateTime(TimeZone.currentSystemDefault()).time
      "${time.hour.toString().padStart(2, '0')}:" +
        "${time.minute.toString().padStart(2, '0')}:" +
        time.second.toString().padStart(2, '0')
    }
    TimeTextFormat.LocalizedDateTime -> formatLocalizedDateTime(instant)
    TimeTextFormat.UnixSeconds -> instant.epochSeconds.toString()
    TimeTextFormat.UnixMilliseconds -> instant.toEpochMilliseconds().toString()
    TimeTextFormat.Custom -> formatCustomDateTime(instant, customPattern) ?: customPattern
  }

internal fun updateIntervalMillis(format: TimeTextFormat): Long =
  when (format) {
    TimeTextFormat.UnixMilliseconds -> 16L
    else -> 1_000L
  }
