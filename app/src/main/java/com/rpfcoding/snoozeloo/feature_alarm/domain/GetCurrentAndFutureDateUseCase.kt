package com.rpfcoding.snoozeloo.feature_alarm.domain

import java.time.LocalDateTime

typealias CurrentAndFutureDate = Pair<LocalDateTime, LocalDateTime>

class GetCurrentAndFutureDateUseCase {

    operator fun invoke(hour: Int, minute: Int): CurrentAndFutureDate {
        val curDateTime = LocalDateTime.now()

        val addedDay = if (hour <= curDateTime.hour && minute <= curDateTime.minute) {
            1L // If current time is 09:30 and the set time is 09:20. Then it will alarm tomorrow.
        } else {
            0
        }
        val futureDateTime = curDateTime
            .plusDays(addedDay)
            .withHour(hour)
            .withMinute(minute)
            .withSecond(0)

        return Pair(curDateTime, futureDateTime)
    }
}