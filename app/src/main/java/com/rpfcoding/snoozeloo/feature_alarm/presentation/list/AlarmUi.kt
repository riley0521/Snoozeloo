package com.rpfcoding.snoozeloo.feature_alarm.presentation.list

import com.rpfcoding.snoozeloo.feature_alarm.domain.Alarm

data class AlarmUi(
    val alarm: Alarm,
    val timeLeftInSeconds: Long,
    val timeToSleepInSeconds: Long?
)