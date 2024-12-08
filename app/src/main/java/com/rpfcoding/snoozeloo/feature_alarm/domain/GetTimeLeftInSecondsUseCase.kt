package com.rpfcoding.snoozeloo.feature_alarm.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.minutes

class GetTimeLeftInSecondsUseCase {

    operator fun invoke(futureDateTime: LocalDateTime): Flow<Long> {
        return flow {
            while (true) {
                val curDateTime = LocalDateTime.now()
                val seconds = ChronoUnit.SECONDS.between(curDateTime, futureDateTime)
                emit(seconds)

                delay(1.minutes)
            }
        }
    }
}