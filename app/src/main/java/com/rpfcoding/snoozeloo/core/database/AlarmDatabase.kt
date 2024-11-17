package com.rpfcoding.snoozeloo.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rpfcoding.snoozeloo.core.database.alarm.AlarmDao
import com.rpfcoding.snoozeloo.core.database.alarm.AlarmEntity

@Database(
    entities = [AlarmEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AlarmDatabase: RoomDatabase() {

    abstract val alarmDao: AlarmDao
}