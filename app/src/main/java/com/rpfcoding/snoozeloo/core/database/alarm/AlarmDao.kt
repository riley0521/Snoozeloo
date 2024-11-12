package com.rpfcoding.snoozeloo.core.database.alarm

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Upsert
    suspend fun upsert(alarm: AlarmEntity)

    @Query("SELECT * FROM tbl_alarms ORDER BY hour ASC")
    fun getAll(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM tbl_alarms WHERE id = :id")
    suspend fun getById(id: String): AlarmEntity?

    @Query("UPDATE tbl_alarms SET enabled = 0 WHERE id = :id")
    suspend fun disableAlarmById(id: String)

    @Query("DELETE FROM tbl_alarms WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM tbl_alarms")
    suspend fun deleteAll()
}