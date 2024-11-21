package com.rpfcoding.snoozeloo.feature_alarm.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * If the alarm is tomorrow and is set between 4am to 10am.
 * Returns the hour that user should sleep.
 */
class GetTimeToSleepInSecondsUseCase(
    private val now: LocalDateTime? = null
) {

    operator fun invoke(hour: Int, futureDateTime: LocalDateTime): Flow<Long?> {
        return flow {
            while (true) {
                delay(100L)
                emit(getHourToSleep(hour, futureDateTime))
            }
        }.distinctUntilChanged()
    }

    private fun getHourToSleep(
        hour: Int,
        futureDateTime: LocalDateTime
    ): Long? {
        if (hour !in 4..10) {
            return null
        }

        val curDateTime = now ?: LocalDateTime.now()
        val tomorrow = curDateTime.plusDays(1)

        // If future dayOfYear is not equals to dayOfYear tomorrow, we don't have to tell user when to sleep.
        // Also, if time is set to 10am tomorrow, the sleep time is 2am which is same day.
        if (tomorrow.dayOfYear != futureDateTime.dayOfYear && curDateTime.dayOfYear != futureDateTime.dayOfYear) {
            return null
        }

        val timeDiff = ChronoUnit.HOURS.between(curDateTime, futureDateTime)
        if (timeDiff < 8) {
            return null
        }

        return convertLocalDateTimeToEpochSeconds(futureDateTime.plusHours(-8))
    }
}