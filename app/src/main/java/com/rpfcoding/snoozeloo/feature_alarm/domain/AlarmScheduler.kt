package com.rpfcoding.snoozeloo.feature_alarm.domain

interface AlarmScheduler {
    fun schedule(alarm: Alarm)
    fun cancel(alarm: Alarm)
}