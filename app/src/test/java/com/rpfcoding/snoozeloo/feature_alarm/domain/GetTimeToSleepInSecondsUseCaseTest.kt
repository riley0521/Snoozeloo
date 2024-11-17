package com.rpfcoding.snoozeloo.feature_alarm.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class GetTimeToSleepInSecondsUseCaseTest {

    private lateinit var getTimeToSleepInSecondsUseCase: GetTimeToSleepInSecondsUseCase

    @Test
    fun `test set alarm to 4am tomorrow, today is 3pm`() = runTest {
        val curDateTime = LocalDateTime.of(2024, 11, 15, 17, 0)
        getTimeToSleepInSecondsUseCase = GetTimeToSleepInSecondsUseCase(now = curDateTime)

        val futureDateTime = curDateTime.plusDays(1)
        val seconds = getTimeToSleepInSecondsUseCase(4, futureDateTime.withHour(4)).first()
        val dateTime = seconds.toLocalDateTime()

        Assert.assertEquals(20, dateTime?.hour)
    }

    @Test
    fun `test set alarm to 10am tomorrow, today is 1am`() = runTest {
        val curDateTime = LocalDateTime.of(2024, 11, 15, 1, 0)
        getTimeToSleepInSecondsUseCase = GetTimeToSleepInSecondsUseCase(now = curDateTime)

        val seconds = getTimeToSleepInSecondsUseCase(10, curDateTime.withHour(10)).first()
        val dateTime = seconds.toLocalDateTime()

        Assert.assertEquals(2, dateTime?.hour)
    }

    private fun Long?.toLocalDateTime(): LocalDateTime? {
        return this?.let {
            Instant.ofEpochSecond(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
    }
}