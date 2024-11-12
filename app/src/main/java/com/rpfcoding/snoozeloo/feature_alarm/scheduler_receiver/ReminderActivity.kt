package com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.rpfcoding.snoozeloo.core.presentation.designsystem.SnoozelooTheme
import com.rpfcoding.snoozeloo.core.util.Constants
import com.rpfcoding.snoozeloo.core.util.hideNotification
import com.rpfcoding.snoozeloo.core.util.isOreoMr1Plus
import com.rpfcoding.snoozeloo.core.util.isOreoPlus
import com.rpfcoding.snoozeloo.feature_alarm.domain.Alarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class ReminderActivity : ComponentActivity() {

    private val scope: CoroutineScope by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        showOverLockscreen()
        val alarmId = intent?.getStringExtra(Constants.EXTRA_ALARM_ID) ?: throw Exception("Alarm ID is not found.")

        setContent {
            SnoozelooTheme {
                val viewModel: ReminderViewModel = koinViewModel()
                var effectsSet by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    viewModel.getAlarmById(alarmId)
                    delay(500L)
                    effectsSet = false
                }

                LaunchedEffect(Unit) {
                    delay(Constants.ALARM_MAX_REMINDER_MILLIS)
                    hideNotification(alarmId.hashCode())
                    finish()
                }

                LaunchedEffect(viewModel.alarm, effectsSet) {
                    if (!effectsSet) {
                        effectsSet = true
                        setupEffects(viewModel.alarm)
                    }
                }

                if (viewModel.alarm != null) {
                    AlarmTriggerScreen(
                        alarm = viewModel.alarm!!,
                        onTurnOffClick = {
                            viewModel.disableAlarm(alarmId)
                            hideNotification(alarmId.hashCode())
                            finish()
                        }
                    )
                }

//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Button(
//                        onClick = {
//                            viewModel.disableAlarm(alarmId)
//                            finish()
//                        }
//                    ) {
//                        Text("Turn Off")
//                    }
//                }
            }
        }
    }

    private fun setupEffects(alarm: Alarm?) {
        val audioManager = getSystemService(AudioManager::class.java)
        // TODO: In extended version, we should get the volume from the alarmItem
        val alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, alarmVolume, 0)

        val pattern = Constants.VIBRATE_PATTERN_LONG_ARR
        if (isOreoPlus()) {
            scope.launch {
                delay(500L)
                val vibrator = getSystemService(Vibrator::class.java)
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
            }
        }

        val ringtoneUri = alarm?.ringtoneUri?.let {
            if (it.isNotBlank()) {
                return@let Uri.parse(it)
            }

            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        } ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        if (ringtoneUri != null && !AlarmReceiver.isPlaying()) {
            MediaPlayer().apply {
                setAudioStreamType(AudioManager.STREAM_ALARM)
                setDataSource(this@ReminderActivity, ringtoneUri)
                isLooping = true
                prepare()
                start()
            }
        }
    }

    private fun showOverLockscreen() {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        if (isOreoMr1Plus()) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
    }
}