package com.rpfcoding.snoozeloo.feature_alarm.domain

import kotlinx.coroutines.flow.Flow

interface LocalAlarmDataSource {
    suspend fun upsert(alarm: Alarm)
    fun getAll(): Flow<List<Alarm>>
    suspend fun getById(id: String): Alarm?
    suspend fun deleteById(id: String)
    suspend fun disableAlarmById(id: String)
    suspend fun deleteAll()
}