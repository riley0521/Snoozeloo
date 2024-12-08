package com.rpfcoding.snoozeloo.core.database

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun setToString(value: Set<Int>): String {
        return Json.encodeToString(value.sorted())
    }

    @TypeConverter
    fun stringToSet(value: String): Set<Int> {
        return Json.decodeFromString<Set<Int>>(value)
    }
}