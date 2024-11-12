package com.rpfcoding.snoozeloo.feature_alarm.data.di

import com.rpfcoding.snoozeloo.feature_alarm.data.AlarmRepositoryImpl
import com.rpfcoding.snoozeloo.feature_alarm.data.RoomLocalAlarmDataSource
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmRepository
import com.rpfcoding.snoozeloo.feature_alarm.domain.GetCurrentAndFutureDateUseCase
import com.rpfcoding.snoozeloo.feature_alarm.domain.GetTimeLeftInSecondsUseCase
import com.rpfcoding.snoozeloo.feature_alarm.domain.LocalAlarmDataSource
import com.rpfcoding.snoozeloo.feature_alarm.domain.ValidateAlarmUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featureAlarmDataModule = module {
    singleOf(::RoomLocalAlarmDataSource).bind<LocalAlarmDataSource>()
    singleOf(::AlarmRepositoryImpl).bind<AlarmRepository>()
    singleOf(::ValidateAlarmUseCase)
    singleOf(::GetCurrentAndFutureDateUseCase)
    singleOf(::GetTimeLeftInSecondsUseCase)
}