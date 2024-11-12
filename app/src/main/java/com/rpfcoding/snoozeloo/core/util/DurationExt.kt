package com.rpfcoding.snoozeloo.core.util

import kotlin.time.Duration
import kotlin.time.DurationUnit

fun Duration.getInt(unit: DurationUnit): Int {
    return this.toLong(unit).toInt()
}

fun Duration.getRemainingDays(): Int {
    return this.getInt(DurationUnit.DAYS)
}

fun Duration.getRemainingHours(): Int {
    return this.getInt(DurationUnit.HOURS) % 24
}

fun Duration.getRemainingMinutes(): Int {
    return this.getInt(DurationUnit.MINUTES) % 60
}