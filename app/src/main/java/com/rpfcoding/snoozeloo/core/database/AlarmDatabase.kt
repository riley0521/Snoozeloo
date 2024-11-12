package com.rpfcoding.snoozeloo.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rpfcoding.snoozeloo.core.database.alarm.AlarmDao
import com.rpfcoding.snoozeloo.core.database.alarm.AlarmEntity

@Database(
    entities = [AlarmEntity::class],
    version = 1
)
abstract class AlarmDatabase: RoomDatabase() {

    abstract val alarmDao: AlarmDao
}