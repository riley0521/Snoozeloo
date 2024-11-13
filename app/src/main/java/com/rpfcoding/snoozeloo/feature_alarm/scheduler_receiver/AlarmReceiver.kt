package com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.rpfcoding.snoozeloo.R
import com.rpfcoding.snoozeloo.core.util.hideNotification
import com.rpfcoding.snoozeloo.core.util.isOreoPlus
import com.rpfcoding.snoozeloo.core.util.isPiePlus
import com.rpfcoding.snoozeloo.core.util.isScreenOn
import com.rpfcoding.snoozeloo.feature_alarm.domain.Alarm
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmConstants
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmReceiver: BroadcastReceiver() {

    private val helper by lazy { AlarmReceiverHelper() }

    companion object {
        private var ringtone: Ringtone? = null

        fun stopAndResetRingtone() {
            ringtone?.stop()
            ringtone = null
        }

        fun isPlaying(): Boolean {
            return ringtone != null
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        helper.onReceive(context, intent)
    }

    private class AlarmReceiverHelper: KoinComponent {

        private val alarmRepository: AlarmRepository by inject()
        private val scope: CoroutineScope by inject()

        fun onReceive(context: Context?, intent: Intent?) {
            val alarmId = intent?.getStringExtra(AlarmConstants.EXTRA_ALARM_ID) ?: return
            if (context == null) {
                return
            }

            val reminderActIntent = Intent(context, ReminderActivity::class.java).apply {
                putExtra(AlarmConstants.EXTRA_ALARM_ID, alarmId)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                reminderActIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (context.isScreenOn()) {
                println("SCREEN ON")

                scope.launch {
                    val alarm = alarmRepository.getById(alarmId)
                    alarm?.let {
                        withContext(Dispatchers.Main) {
                            showAlarmNotification(context, pendingIntent, alarm)
                        }

                        launch {
                            delay(AlarmConstants.ALARM_MAX_REMINDER_MILLIS)
                            context.hideNotification(alarm.id.hashCode())
                        }
                    }
                }
            } else {
                println("SCREEN OFF")

                if (isOreoPlus()) {
                    val notificationManager = context.getSystemService(NotificationManager::class.java)
                    val builder = NotificationCompat.Builder(context, AlarmConstants.CHANNEL_ID)
                        .setSmallIcon(R.drawable.alarm)
                        .setContentTitle("Alarm")
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setFullScreenIntent(pendingIntent, true)

                    notificationManager.notify(alarmId.hashCode(), builder.build())
                } else {
                    context.startActivity(reminderActIntent)
                }
            }
        }

        private fun showAlarmNotification(context: Context, pendingIntent: PendingIntent, alarm: Alarm) {
            val notification = buildAlarmNotification(context, pendingIntent, alarm)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.notify(alarm.id.hashCode(), notification)
        }

        private fun buildAlarmNotification(context: Context, pendingIntent: PendingIntent, alarm: Alarm): Notification {
            val ringtoneUri: Uri = alarm.ringtoneUri.let {
                if (it.isNotBlank()) {
                    return@let Uri.parse(it)
                }

                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            }
            val channelId = "alarm-${alarm.id}"

            ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
            ringtone?.audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setLegacyStreamType(AudioManager.STREAM_ALARM)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .build()

            // TODO: In extended version, we should get the volume from the alarmItem
            if (isPiePlus()) {
                ringtone?.volume = 0.7f
            }
            ringtone?.play()

            if (isOreoPlus()) {
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                val channel = NotificationChannel(channelId, alarm.name, NotificationManager.IMPORTANCE_HIGH).apply {
                    setBypassDnd(true)
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            val dismissAlarmPendingIntent = getDismissAlarmPendingIntent(context, alarm, channelId)
            val vibrateArray = AlarmConstants.VIBRATE_PATTERN_LONG_ARR

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle(alarm.name)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setChannelId(channelId)
                .addAction(-1, "Turn off", dismissAlarmPendingIntent)
                .setDeleteIntent(dismissAlarmPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVibrate(vibrateArray)

            return builder.build().apply {
                flags = flags or Notification.FLAG_INSISTENT
            }
        }

        private fun getDismissAlarmPendingIntent(context: Context, alarm: Alarm, channelId: String): PendingIntent {
            val intent = Intent(context, DismissAlarmReceiver::class.java).apply {
                putExtra(AlarmConstants.EXTRA_ALARM_ID, alarm.id)
                putExtra(AlarmConstants.EXTRA_ALARM_CUSTOM_CHANNEL_ID, channelId)
            }
            return PendingIntent.getBroadcast(
                context,
                alarm.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}