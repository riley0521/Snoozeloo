package com.rpfcoding.snoozeloo.feature_alarm.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

class GetTimeLeftInSecondsUseCase {

    operator fun invoke(futureDateTime: LocalDateTime): Flow<Long> {
        return flow {
            while (true) {
                delay(100L)

                val curDateTime = LocalDateTime.now()
                emit(
                    convertLocalDateTimeToEpochSeconds(futureDateTime) - convertLocalDateTimeToEpochSeconds(curDateTime)
                )
            }
        }
    }
}