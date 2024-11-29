package com.rpfcoding.snoozeloo.feature_alarm.domain

object AlarmConstants {

    const val CHANNEL_ID = "snoozeloo_alarm"
    const val EXTRA_ALARM_ID = "SNOOZELOO_ALARM_ID"
    const val EXTRA_ALARM_CUSTOM_CHANNEL_ID = "SNOOZELOO_ALARM_CUSTOM_CHANNEL_ID"
    const val EXTRA_SHOULD_SNOOZE = "SNOOZELOO_SHOULD_SNOOZE"
    val VIBRATE_PATTERN_LONG_ARR = LongArray(2) { 500 }
    const val SUFFIX_SNOOZE = "-Snooze"
    const val SUFFIX_DISABLE = "-Disable"
}