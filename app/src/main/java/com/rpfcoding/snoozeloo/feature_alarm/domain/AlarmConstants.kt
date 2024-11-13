package com.rpfcoding.snoozeloo.feature_alarm.domain

object AlarmConstants {

    const val CHANNEL_ID = "snoozeloo_alarm"
    const val EXTRA_ALARM_ID = "SNOOZELOO_ALARM_ID"
    const val EXTRA_ALARM_CUSTOM_CHANNEL_ID = "SNOOZELOO_ALARM_CUSTOM_CHANNEL_ID"
    const val ALARM_MAX_REMINDER_MILLIS = 300_000L
    val VIBRATE_PATTERN_LONG_ARR = LongArray(2) { 500 }
}