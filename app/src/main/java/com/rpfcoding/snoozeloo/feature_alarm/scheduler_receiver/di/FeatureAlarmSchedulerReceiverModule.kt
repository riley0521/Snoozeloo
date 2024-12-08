package com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver.di

import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmScheduler
import com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver.AndroidAlarmScheduler
import com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver.ReminderViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val featureAlarmSchedulerReceiverModule = module {
    viewModel { (alarmId: String) -> ReminderViewModel(alarmId, get()) }
    single<AndroidAlarmScheduler> {
        AndroidAlarmScheduler(androidContext(), get())
    }.bind<AlarmScheduler>()
}