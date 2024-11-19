package com.rpfcoding.snoozeloo.core.ringtone

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import com.rpfcoding.snoozeloo.core.domain.ringtone.NameAndUri
import com.rpfcoding.snoozeloo.core.domain.ringtone.RingtoneManager
import com.rpfcoding.snoozeloo.core.domain.ringtone.SILENT

class AndroidRingtoneManager(
    private val context: Context
): RingtoneManager {

    private var mediaPlayer: MediaPlayer? = null

    override fun getAvailableRingtones(): List<NameAndUri> {
        val ringtoneManager = android.media.RingtoneManager(context).apply {
            setType(android.media.RingtoneManager.TYPE_ALARM)
        }

        val defaultRingtoneUri = android.media.RingtoneManager
            .getActualDefaultRingtoneUri(context, android.media.RingtoneManager.TYPE_ALARM)
            .buildUpon()
            .clearQuery()
            .build()
        var defaultRingtoneName = ""

        val cursor = ringtoneManager.cursor

        val ringtones = mutableListOf<NameAndUri>()
        ringtones.add(Pair("Silent", SILENT))

        while (cursor.moveToNext()) {
            val id = cursor.getString(android.media.RingtoneManager.ID_COLUMN_INDEX)
            val title = cursor.getString(android.media.RingtoneManager.TITLE_COLUMN_INDEX)
            val uri = cursor.getString(android.media.RingtoneManager.URI_COLUMN_INDEX)
            val fullUri = "$uri/$id"

            if (fullUri != defaultRingtoneUri.toString()) {
                ringtones.add(Pair(title, fullUri))
            } else {
                defaultRingtoneName = title
            }
        }

        if (ringtones.size >= 2) {
            ringtones.add(1, Pair("Default (${defaultRingtoneName})", defaultRingtoneUri.toString()))
        }

        return ringtones.also {
            println("($defaultRingtoneName, $defaultRingtoneUri)")
            it.mapIndexed { index, pair ->
                println("$index : $pair")
            }
        }
    }

    override fun play(uri: String, isLooping: Boolean, volume: Float) {
        val fullUri: Uri = try {
            if (uri == SILENT) {
                null
            } else {
                Uri.parse(uri)
            }
        } catch (e: Exception) {
            null
        } ?: return

        if (isPlaying()) {
            stop()
        }

        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_ALARM)
            setDataSource(context, fullUri)
            setVolume(volume, volume)
            prepare()
            start()
            this.isLooping = isLooping
        }
    }

    override fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun isPlaying(): Boolean {
        return try {
             mediaPlayer?.isPlaying == true
        } catch (e: Exception) {
            false
        }
    }
}