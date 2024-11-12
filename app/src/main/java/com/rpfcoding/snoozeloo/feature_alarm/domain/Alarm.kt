package com.rpfcoding.snoozeloo.feature_alarm.domain

import java.util.UUID

data class Alarm(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean,
    val ringtoneUri: String
) {
    val isMorning: Boolean get() = hour in 0..11

    val hourTwelve: Int get() = if(hour > 12) {
        hour - 12
    } else {
        hour
    }
}
