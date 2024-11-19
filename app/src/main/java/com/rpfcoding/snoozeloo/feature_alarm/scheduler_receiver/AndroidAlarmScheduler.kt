package com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.rpfcoding.snoozeloo.feature_alarm.domain.Alarm
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmConstants
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmScheduler
import com.rpfcoding.snoozeloo.feature_alarm.domain.GetFutureDateUseCase
import java.time.LocalDateTime
import java.time.ZoneId

class AndroidAlarmScheduler(
    private val context: Context,
    private val getFutureDateUseCase: GetFutureDateUseCase
): AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @SuppressLint("MissingPermission")
    override fun schedule(alarm: Alarm, shouldSnooze: Boolean) {
        val futureDateTime = getFutureDateUseCase(
            hour = alarm.hour,
            minute = alarm.minute,
            repeatDays = alarm.repeatDays
        )

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmConstants.EXTRA_ALARM_ID, alarm.id)
        }

        val curDateTime = LocalDateTime.now()
        val triggerAtMillis = if (shouldSnooze) {
            curDateTime
                .plusMinutes(5)
                .withSecond(0)
                .atZone(ZoneId.systemDefault())
                .toEpochSecond() * 1_000L
        } else {
            futureDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1_000L
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            PendingIntent.getBroadcast(
                context,
                alarm.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(alarm: Alarm) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alarm.id.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}