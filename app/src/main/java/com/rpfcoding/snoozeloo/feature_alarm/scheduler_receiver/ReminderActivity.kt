package com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.rpfcoding.snoozeloo.core.presentation.designsystem.SnoozelooTheme
import com.rpfcoding.snoozeloo.core.presentation.ui.ObserveAsEvents
import com.rpfcoding.snoozeloo.core.util.isOreoMr1Plus
import com.rpfcoding.snoozeloo.feature_alarm.domain.Alarm
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmConstants
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class ReminderActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        showOverLockscreen()
        val alarmId = intent?.getStringExtra(AlarmConstants.EXTRA_ALARM_ID) ?: throw Exception("Alarm ID is not found.")

        setContent {
            SnoozelooTheme {
                val viewModel: ReminderViewModel = koinViewModel { parametersOf(alarmId) }
                var alarm by remember { mutableStateOf<Alarm?>(null) }

                ObserveAsEvents(viewModel.events) { event ->
                    when (event) {
                        is ReminderEvent.OnAlarmFetched -> {
                            alarm = event.alarm
                        }
                        else -> finish()
                    }
                }

                alarm?.let {
                    AlarmTriggerScreen(
                        alarm = it,
                        onTurnOffClick = {
                            viewModel.disableOrRescheduleAlarm()
                            finish()
                        },
                        onSnoozeClick = {
                            viewModel.snoozeAlarm()
                            finish()
                        }
                    )
                }
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