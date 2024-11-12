package com.rpfcoding.snoozeloo.feature_alarm.data

import com.rpfcoding.snoozeloo.core.database.alarm.AlarmDao
import com.rpfcoding.snoozeloo.feature_alarm.data.mapper.toAlarm
import com.rpfcoding.snoozeloo.feature_alarm.data.mapper.toAlarmEntity
import com.rpfcoding.snoozeloo.feature_alarm.domain.Alarm
import com.rpfcoding.snoozeloo.feature_alarm.domain.LocalAlarmDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLocalAlarmDataSource(
    private val alarmDao: AlarmDao
): LocalAlarmDataSource {
    override suspend fun upsert(alarm: Alarm) {
        alarmDao.upsert(alarm.toAlarmEntity())
    }

    override fun getAll(): Flow<List<Alarm>> {
        return alarmDao.getAll().map { alarms ->
            alarms.map { it.toAlarm() }
        }
    }

    override suspend fun getById(id: String): Alarm? {
        return alarmDao.getById(id)?.toAlarm()
    }

    override suspend fun deleteById(id: String) {
        alarmDao.deleteById(id)
    }

    override suspend fun disableAlarmById(id: String) {
        alarmDao.disableAlarmById(id)
    }

    override suspend fun deleteAll() {
        alarmDao.deleteAll()
    }
}