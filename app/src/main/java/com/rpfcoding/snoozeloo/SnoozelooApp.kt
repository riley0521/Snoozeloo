package com.rpfcoding.snoozeloo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.rpfcoding.snoozeloo.core.database.di.coreDatabaseModule
import com.rpfcoding.snoozeloo.core.ringtone.di.coreRingtoneModule
import com.rpfcoding.snoozeloo.core.util.isOreoPlus
import com.rpfcoding.snoozeloo.feature_alarm.data.di.featureAlarmDataModule
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmConstants
import com.rpfcoding.snoozeloo.feature_alarm.presentation.di.featureAlarmPresentationModule
import com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver.di.featureAlarmSchedulerReceiverModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SnoozelooApp: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        startKoin {
            androidLogger()
            androidContext(this@SnoozelooApp)
            modules(
                appModule,
                coreDatabaseModule,
                coreRingtoneModule,
                featureAlarmDataModule,
                featureAlarmPresentationModule,
                featureAlarmSchedulerReceiverModule
            )
        }
    }

    private fun createNotificationChannel() {
        if (isOreoPlus()) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                AlarmConstants.CHANNEL_ID,
                "Alarm",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setBypassDnd(true)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}