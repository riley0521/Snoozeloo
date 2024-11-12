package com.rpfcoding.snoozeloo.feature_alarm.domain

class ValidateAlarmUseCase {

    operator fun invoke(hour: String, minute: String): Boolean {
        val hourInt = hour.toIntOrNull() ?: return false
        val minuteInt = minute.toIntOrNull() ?: return false

        return hourInt in 0..23 &&
                minuteInt in 0..59
    }
}