package com.rpfcoding.snoozeloo.feature_alarm.domain

import java.util.UUID

/**
 * @param hour Value can be from 0-23
 * @param minute Value can be from 0-59
 * @param enabled If true, it will be scheduled in AlarmManager
 * @param repeatDays 0 is Monday, 1 is Tuesday, so on...
 * @param volume Can be 0-100
 */
data class Alarm(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean,
    val repeatDays: Set<DayValue>,
    val volume: Int,
    val ringtoneUri: String,
    val vibrate: Boolean
) {
    val isMorning: Boolean get() = hour in 0..11

    /**
     * Get the hour in 12-hour format. 13 = 1 PM
     */
    val hourTwelve: Int get() = if(hour > 12) {
        hour - 12
    } else {
        hour
    }

    val isOneTime: Boolean get() = repeatDays.isEmpty()
}
