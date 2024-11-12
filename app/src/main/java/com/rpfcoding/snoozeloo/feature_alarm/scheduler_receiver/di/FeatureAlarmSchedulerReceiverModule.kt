package com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver.di

import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmScheduler
import com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver.AndroidAlarmScheduler
import com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver.ReminderViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featureAlarmSchedulerReceiverModule = module {
    viewModelOf(::ReminderViewModel)
    single<AndroidAlarmScheduler> {
        AndroidAlarmScheduler(androidContext(), get())
    }.bind<AlarmScheduler>()
}