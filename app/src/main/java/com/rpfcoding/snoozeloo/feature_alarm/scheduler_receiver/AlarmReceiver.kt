package com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.rpfcoding.snoozeloo.R
import com.rpfcoding.snoozeloo.core.domain.ringtone.ALARM_MAX_REMINDER_MILLIS
import com.rpfcoding.snoozeloo.core.util.isOreoPlus
import com.rpfcoding.snoozeloo.core.util.isScreenOn
import com.rpfcoding.snoozeloo.feature_alarm.domain.Alarm
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmConstants
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmRepository
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.rpfcoding.snoozeloo.core.domain.ringtone.RingtoneManager as MyRingtoneManager

class AlarmReceiver: BroadcastReceiver() {

    private val helper by lazy { AlarmReceiverHelper() }

    override fun onReceive(context: Context?, intent: Intent?) {
        helper.onReceive(context, intent)
    }

    private class AlarmReceiverHelper: KoinComponent {

        private val alarmRepository: AlarmRepository by inject()
        private val ringtoneManager: MyRingtoneManager by inject()
        private val handler by lazy { Handler(Looper.getMainLooper()) }

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

                val alarm = runBlocking { alarmRepository.getById(alarmId) }
                alarm?.let {
                    showAlarmNotification(context, pendingIntent, alarm)

                    handler.postDelayed(
                        {
                            val dismissAlarmIntent = Intent(context, DismissAlarmReceiver::class.java).apply {
                                putExtra(AlarmConstants.EXTRA_ALARM_ID, alarm.id)
                                putExtra(AlarmConstants.EXTRA_ALARM_CUSTOM_CHANNEL_ID, alarm.id)
                                putExtra(AlarmConstants.EXTRA_SHOULD_SNOOZE, true)
                            }
                            context.sendBroadcast(dismissAlarmIntent)
                        },
                        ALARM_MAX_REMINDER_MILLIS
                    )
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
            val channelId = alarm.id

            ringtoneManager.play(
                uri = ringtoneUri.toString(),
                isLooping = true,
                volume = alarm.volume / 100f
            )
            if (isOreoPlus()) {
                val alarmName = alarm.name.ifBlank { "Alarm" }
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                val channel = NotificationChannel(channelId, alarmName, NotificationManager.IMPORTANCE_HIGH).apply {
                    setBypassDnd(true)
                    enableVibration(alarm.vibrate)
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

            if (alarm.vibrate) {
                builder.setVibrate(vibrateArray) // Ignored in Android O above since we set the vibration in NotificationChannel.
            } else {
                builder.setVibrate(LongArray(1) {0L})
            }

            return builder.build()
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