package com.rpfcoding.snoozeloo.feature_alarm.data.mapper

import com.rpfcoding.snoozeloo.core.database.alarm.AlarmEntity
import com.rpfcoding.snoozeloo.feature_alarm.domain.Alarm

fun Alarm.toAlarmEntity(): AlarmEntity {
    return AlarmEntity(
        id = id,
        name = name,
        hour = hour,
        minute = minute,
        enabled = enabled,
        ringtoneUri = ringtoneUri
    )
}

fun AlarmEntity.toAlarm(): Alarm {
    return Alarm(
        id = id,
        name = name,
        hour = hour,
        minute = minute,
        enabled = enabled,
        ringtoneUri = ringtoneUri
    )
}