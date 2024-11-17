package com.rpfcoding.snoozeloo.feature_alarm.presentation.list

import com.rpfcoding.snoozeloo.feature_alarm.domain.Alarm
import com.rpfcoding.snoozeloo.feature_alarm.domain.DayValue

sealed interface AlarmListAction {
    data object OnAddNewAlarmClick: AlarmListAction
    data class OnToggleAlarm(val alarm: Alarm): AlarmListAction
    data class OnToggleDayOfAlarm(val day: DayValue, val alarm: Alarm): AlarmListAction
    data class OnAlarmClick(val id: String): AlarmListAction
    data class OnDeleteAlarmClick(val id: String): AlarmListAction
}