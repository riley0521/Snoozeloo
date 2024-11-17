package com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver

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
import com.rpfcoding.snoozeloo.core.domain.ringtone.ALARM_MAX_REMINDER_MILLIS
import com.rpfcoding.snoozeloo.core.presentation.designsystem.SnoozelooTheme
import com.rpfcoding.snoozeloo.core.util.hideNotification
import com.rpfcoding.snoozeloo.core.util.isOreoMr1Plus
import com.rpfcoding.snoozeloo.core.util.isOreoPlus
import com.rpfcoding.snoozeloo.feature_alarm.domain.Alarm
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.rpfcoding.snoozeloo.core.domain.ringtone.RingtoneManager as MyRingtoneManager

class ReminderActivity : ComponentActivity() {

    private val viewModel: ReminderViewModel by viewModel()
    private val ringtoneManager: MyRingtoneManager by inject()
    private val scope: CoroutineScope by inject()
    private val vibrator by lazy {
        getSystemService(Vibrator::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        showOverLockscreen()
        val alarmId = intent?.getStringExtra(AlarmConstants.EXTRA_ALARM_ID) ?: throw Exception("Alarm ID is not found.")

        setContent {
            SnoozelooTheme {
                var effectsSet by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    viewModel.getAlarmById(alarmId)
                    delay(500L)
                    effectsSet = false
                }

                LaunchedEffect(Unit) {
                    delay(ALARM_MAX_REMINDER_MILLIS)
                    disableAlarmAndFinish(alarmId)
                }

                LaunchedEffect(viewModel.alarm, effectsSet) {
                    if (!effectsSet && viewModel.alarm != null) {
                        effectsSet = true
                        setupEffects(viewModel.alarm!!)
                    }
                }

                if (viewModel.alarm != null) {
                    AlarmTriggerScreen(
                        alarm = viewModel.alarm!!,
                        onTurnOffClick = {
                            disableAlarmAndFinish(alarmId)
                        }
                    )
                }
            }
        }
    }

    private fun disableAlarmAndFinish(alarmId: String) {
        viewModel.disableAlarm(alarmId)
        hideNotification(alarmId.hashCode())
        ringtoneManager.stop()
        vibrator.cancel()
        finish()
    }

    private fun setupEffects(alarm: Alarm) {
        val pattern = AlarmConstants.VIBRATE_PATTERN_LONG_ARR
        if (isOreoPlus() && alarm.vibrate) {
            scope.launch {
                delay(500L)
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
            }
        }

        val ringtoneUri = alarm.ringtoneUri.let {
            if (it.isNotBlank()) {
                return@let Uri.parse(it)
            }

            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }

        val volume = (alarm.volume / 100f)
        if (ringtoneUri != null && !ringtoneManager.isPlaying()) {
            ringtoneManager.play(uri = ringtoneUri.toString(), isLooping = true, volume = volume)
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