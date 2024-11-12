package com.rpfcoding.snoozeloo.feature_alarm.presentation.di

import com.rpfcoding.snoozeloo.feature_alarm.presentation.add_edit.AddEditAlarmViewModel
import com.rpfcoding.snoozeloo.feature_alarm.presentation.list.AlarmListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureAlarmPresentationModule = module {
    viewModelOf(::AlarmListViewModel)
    viewModelOf(::AddEditAlarmViewModel)
}