package com.rpfcoding.snoozeloo.core.database.alarm

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean,
    val repeatDays: Set<Int>,
    val volume: Int,
    val ringtoneUri: String,
    val vibrate: Boolean
)
