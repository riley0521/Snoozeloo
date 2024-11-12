package com.rpfcoding.snoozeloo.feature_alarm.presentation.list

import com.rpfcoding.snoozeloo.feature_alarm.domain.Alarm

sealed interface AlarmListAction {
    data object OnAddNewAlarmClick: AlarmListAction
    data class OnToggleAlarm(val alarm: Alarm): AlarmListAction
    data class OnAlarmClick(val id: String): AlarmListAction
    data class OnDeleteAlarmClick(val id: String): AlarmListAction
}