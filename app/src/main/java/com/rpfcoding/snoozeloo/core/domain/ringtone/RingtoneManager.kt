package com.rpfcoding.snoozeloo.core.domain.ringtone

const val SILENT = "silent"
const val ALARM_MAX_REMINDER_MILLIS = 300_000L
typealias NameAndUri = Pair<String, String>

interface RingtoneManager {

    /**
     * @return A list of pair (name of ringtone & uri)
     */
    suspend fun getAvailableRingtones(): List<NameAndUri>

    /**
     * @param uri string representation of sound uri.
     * @param isLooping whether we keep repeating the same sound or not.
     * @param volume must be from 0.0f to 1.0f only.
     */
    fun play(uri: String, isLooping: Boolean = false, volume: Float = 0.7f)

    /**
     * Stop the currently playing sound.
     */
    fun stop()

    /**
     * @return true if there is a sound playing currently.
     */
    fun isPlaying(): Boolean
}