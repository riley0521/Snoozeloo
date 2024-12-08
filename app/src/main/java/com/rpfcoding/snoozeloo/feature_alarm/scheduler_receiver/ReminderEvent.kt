package com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver

import com.rpfcoding.snoozeloo.feature_alarm.domain.Alarm

sealed interface ReminderEvent {
    data class OnAlarmFetched(val alarm: Alarm): ReminderEvent
    data object OnTimerExpired: ReminderEvent
    data object AlarmIsNotExisting: ReminderEvent
}