package com.rpfcoding.snoozeloo.feature_alarm.domain

interface AlarmScheduler {
    fun schedule(alarm: Alarm, shouldSnooze: Boolean = false)
    fun cancel(alarm: Alarm)
}