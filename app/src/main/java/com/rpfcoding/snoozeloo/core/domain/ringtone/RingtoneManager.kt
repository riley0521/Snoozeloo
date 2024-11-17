package com.rpfcoding.snoozeloo.core.domain.ringtone

const val SILENT = "silent"
const val ALARM_MAX_REMINDER_MILLIS = 300_000L
typealias NameAndUri = Pair<String, String>

interface RingtoneManager {

    fun getAvailableRingtones(): List<NameAndUri>
    fun play(uri: String, isLooping: Boolean = false, volume: Float = 0.7f)
    fun stop()
    fun isPlaying(): Boolean
}