package com.rpfcoding.snoozeloo.feature_alarm.domain

import java.time.DayOfWeek
import java.time.LocalDateTime

class GetCurrentAndFutureDateUseCase(
    private val now: LocalDateTime? = null
) {

    operator fun invoke(hour: Int, minute: Int, repeatDays: Set<DayValue> = emptySet()): LocalDateTime {
        val curDateTime = now ?: LocalDateTime.now()
        val futureDateTime = getFutureDateWithRepeatDays(
                curDateTime,
                hour,
                minute,
                repeatDays
            )

        return futureDateTime
    }

    /**
     * We need this function because we might set the alarm on Wednesday, but the repeatDays is on Saturday & Sunday.
     */
    private fun getFutureDateWithRepeatDays(
        curDateTime: LocalDateTime,
        hour: Int,
        minute: Int,
        repeatDays: Set<DayValue>
    ): LocalDateTime {
        var futureDateTime: LocalDateTime = curDateTime
        val isRepeatable = repeatDays.isNotEmpty()

        if (isRepeatable) {
            while (!isDayOfWeekPresentInRepeatDays(futureDateTime.dayOfWeek, repeatDays)) {
                futureDateTime = futureDateTime.plusDays(1)
            }
        }

        return if (curDateTime.dayOfYear != futureDateTime.dayOfYear) {
            futureDateTime
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
        } else {
            val tomorrow = curDateTime.plusDays(1)
            val isTomorrowAvailable = isDayOfWeekPresentInRepeatDays(tomorrow.dayOfWeek, repeatDays)

            // IF the set hour/minute is earlier than current time AND
            // tomorrow is on repeatDays OR isRepeatable == false. Then we can set alarm for tomorrow.
            if (hour <= curDateTime.hour && minute <= curDateTime.minute && (isTomorrowAvailable || !isRepeatable)) {
                tomorrow
                    .withHour(hour)
                    .withMinute(minute)
                    .withSecond(0)
            } else { // We assume here that isRepeatable == true
                getFutureDateWithRepeatDays(tomorrow, hour, minute, repeatDays)
            }
        }
    }

    private fun isDayOfWeekPresentInRepeatDays(dayOfWeek: DayOfWeek, repeatDays: Set<DayValue>): Boolean {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> repeatDays.contains(DayValue.MONDAY)
            DayOfWeek.TUESDAY -> repeatDays.contains(DayValue.TUESDAY)
            DayOfWeek.WEDNESDAY -> repeatDays.contains(DayValue.WEDNESDAY)
            DayOfWeek.THURSDAY -> repeatDays.contains(DayValue.THURSDAY)
            DayOfWeek.FRIDAY -> repeatDays.contains(DayValue.FRIDAY)
            DayOfWeek.SATURDAY -> repeatDays.contains(DayValue.SATURDAY)
            DayOfWeek.SUNDAY -> repeatDays.contains(DayValue.SUNDAY)
        }
    }
}