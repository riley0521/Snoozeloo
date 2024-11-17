package com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rpfcoding.snoozeloo.core.domain.ringtone.RingtoneManager
import com.rpfcoding.snoozeloo.core.util.isOreoPlus
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmConstants
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
        private val ringtoneManager: RingtoneManager by inject()
        private val scope: CoroutineScope by inject()

        fun onReceive(context: Context?, intent: Intent?) {
            val alarmId = intent?.getStringExtra(AlarmConstants.EXTRA_ALARM_ID) ?: return
            if (context == null) {
                return
            }

            ringtoneManager.stop()
            intent.getStringExtra(AlarmConstants.EXTRA_ALARM_CUSTOM_CHANNEL_ID)?.let { channelId ->
                deleteNotificationChannel(context, channelId)
            }

            scope.launch(Dispatchers.Main) {
                val alarm = alarmRepository.getById(alarmId) ?: return@launch

                if (alarm.isOneTime) {
                    alarmRepository.disableAlarmById(alarmId)
                } else {
                    // Just re-insert the same alarm, to schedule it.
                    alarmRepository.upsert(alarm)
                }
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