package com.rpfcoding.snoozeloo.feature_alarm.data

import com.rpfcoding.snoozeloo.feature_alarm.domain.Alarm
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmRepository
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmScheduler
import com.rpfcoding.snoozeloo.feature_alarm.domain.DayValue
import com.rpfcoding.snoozeloo.feature_alarm.domain.LocalAlarmDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class AlarmRepositoryImpl(
    private val localAlarmDataSource: LocalAlarmDataSource,
    private val alarmScheduler: AlarmScheduler
): AlarmRepository {
    override fun getAll(): Flow<List<Alarm>> {
        return localAlarmDataSource.getAll()
    }

    override suspend fun getById(id: String): Alarm? {
        return localAlarmDataSource.getById(id)
    }

    override suspend fun upsert(alarm: Alarm) {
        localAlarmDataSource.upsert(alarm)
        alarmScheduler.schedule(alarm)
    }

    override suspend fun toggle(alarm: Alarm) {
        val isEnabled = !alarm.enabled
        localAlarmDataSource.upsert(alarm.copy(enabled = isEnabled))

        if (isEnabled) {
            alarmScheduler.schedule(alarm)
        } else {
            alarmScheduler.cancel(alarm)
        }
    }

    override suspend fun toggleDay(day: DayValue, alarm: Alarm) {
        // Cancel alarm first to avoid conflict.
        alarmScheduler.cancel(alarm)


        val repeatDays = alarm.repeatDays.toMutableSet()

        // Remove it from the set if it exists.
        if (repeatDays.contains(day)) {
            repeatDays.remove(day)
        } else { // Or else, add it in the set AND schedule the alarm.
            repeatDays.add(day)
            alarmScheduler.schedule(alarm)
        }

        // Finally, update the DB
        localAlarmDataSource.upsert(alarm.copy(repeatDays = repeatDays))
    }

    override suspend fun disableAlarmById(id: String) {
        localAlarmDataSource.disableAlarmById(id)
    }

    override suspend fun deleteById(id: String) {
        getById(id)?.let {
            alarmScheduler.cancel(it)
        }

        localAlarmDataSource.deleteById(id)
    }

    override suspend fun scheduleAllEnabledAlarms() {
        withContext(Dispatchers.IO) {
            val setAlarmsDeferred = getAll().first().map { alarm ->
                async {
                    if (alarm.enabled) {
                        alarmScheduler.schedule(alarm)
                    }
                }
            }

            setAlarmsDeferred.awaitAll()
        }
    }
}