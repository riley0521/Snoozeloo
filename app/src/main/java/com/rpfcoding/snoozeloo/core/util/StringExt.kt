package com.rpfcoding.snoozeloo.core.util

import java.time.Instant
import java.time.ZoneId
import java.util.Locale
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun formatHourMinute(hour: Int, minute: Int): String {
    return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
}

fun formatNumberWithLeadingZero(value: Int): String {
    return String.format(Locale.getDefault(), "%02d", value)
}

fun formatSecondsToHourAndMinute(value: Long): String {
    val dateTime = Instant.ofEpochSecond(value).atZone(ZoneId.systemDefault()).toLocalDateTime()
    val suffix = if (dateTime.hour < 12) {
        "AM"
    } else {
        "PM"
    }
    val hourTwelve = if (dateTime.hour > 12) {
        dateTime.hour - 12
    } else {
        dateTime.hour
    }

    return String.format(Locale.getDefault(), "%02d:%02d $suffix", hourTwelve, dateTime.minute)
}

/**
 * @return format 95_670 seconds to 1d 2h 34min
 */
fun formatSeconds(value: Long): String {
    val totalTimeDuration = value.toDuration(DurationUnit.SECONDS)
    val days = totalTimeDuration.getRemainingDays().coerceAtLeast(0)
    val hours = totalTimeDuration.getRemainingHours().coerceAtLeast(0)
    val minutes = totalTimeDuration.getRemainingMinutes()

    val dayStr = "${days}d"
    val hourStr = "${hours}h"
    val minuteStr = "${minutes}min"

    return when {
        days > 0 -> {
            if (hours > 0) {
                if (minutes > 0) {
                    "$dayStr $hourStr $minuteStr"
                } else {
                    "$dayStr $hourStr"
                }
            } else {
                dayStr
            }
        }
        hours > 0 -> {
            if (minutes > 0) {
                "$hourStr $minuteStr"
            } else {
                hourStr
            }
        }
        else -> {
            if (minutes > 0) {
                minuteStr
            } else if (minutes == 0) {
                "less than a minute"
            } else {
                ""
            }
        }
    }
}