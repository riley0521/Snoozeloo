package com.rpfcoding.snoozeloo.feature_alarm.presentation.list

import com.rpfcoding.snoozeloo.feature_alarm.domain.Alarm

data class AlarmListState(
    val alarms: List<Alarm> = emptyList()
)
