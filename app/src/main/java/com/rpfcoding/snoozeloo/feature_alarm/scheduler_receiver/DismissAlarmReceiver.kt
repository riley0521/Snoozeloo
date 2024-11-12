package com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rpfcoding.snoozeloo.core.util.Constants
import com.rpfcoding.snoozeloo.core.util.isOreoPlus
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DismissAlarmReceiver: BroadcastReceiver() {

    private val helper by lazy { DismissAlarmReceiverHelper() }

    override fun onReceive(context: Context?, intent: Intent?) {
        helper.onReceive(context, intent)
    }

    private class DismissAlarmReceiverHelper: KoinComponent {

        private val alarmRepository: AlarmRepository by inject()
        private val scope: CoroutineScope by inject()

        fun onReceive(context: Context?, intent: Intent?) {
            val alarmId = intent?.getStringExtra(Constants.EXTRA_ALARM_ID) ?: return
            if (context == null) {
                return
            }

            AlarmReceiver.stopAndResetRingtone()
            intent.getStringExtra(Constants.EXTRA_ALARM_CUSTOM_CHANNEL_ID)?.let { channelId ->
                deleteNotificationChannel(context, channelId)
            }

            scope.launch(Dispatchers.Main) {
                alarmRepository.disableAlarmById(alarmId)
                // TODO: In extended version, schedule next alarm if eligible.
            }
        }

        private fun deleteNotificationChannel(context: Context, channelId: String) {
            if (isOreoPlus()) {
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.deleteNotificationChannel(channelId)
            }
        }
    }
}