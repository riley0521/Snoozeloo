package com.rpfcoding.snoozeloo.feature_alarm.data.di

import com.rpfcoding.snoozeloo.feature_alarm.data.AlarmRepositoryImpl
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmRepository
import com.rpfcoding.snoozeloo.feature_alarm.domain.GetFutureDateUseCase
import com.rpfcoding.snoozeloo.feature_alarm.domain.GetTimeLeftInSecondsUseCase
import com.rpfcoding.snoozeloo.feature_alarm.domain.GetTimeToSleepInSecondsUseCase
import com.rpfcoding.snoozeloo.feature_alarm.domain.ValidateAlarmUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featureAlarmDataModule = module {
    singleOf(::AlarmRepositoryImpl).bind<AlarmRepository>()
    singleOf(::ValidateAlarmUseCase)
    singleOf(::GetFutureDateUseCase)
    singleOf(::GetTimeLeftInSecondsUseCase)
    singleOf(::GetTimeToSleepInSecondsUseCase)
}