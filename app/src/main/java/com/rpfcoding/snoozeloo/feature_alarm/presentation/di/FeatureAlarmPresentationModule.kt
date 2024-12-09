package com.rpfcoding.snoozeloo.feature_alarm.presentation.di

import com.rpfcoding.snoozeloo.core.domain.ringtone.NameAndUri
import com.rpfcoding.snoozeloo.feature_alarm.presentation.add_edit.AddEditAlarmViewModel
import com.rpfcoding.snoozeloo.feature_alarm.presentation.list.AlarmListViewModel
import com.rpfcoding.snoozeloo.feature_alarm.presentation.ringtone_list.RingtoneListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureAlarmPresentationModule = module {
    viewModelOf(::AlarmListViewModel)
    viewModel { (alarmId: String?) ->
        AddEditAlarmViewModel(
            alarmId = alarmId,
            alarmRepository = get(),
            validateAlarmUseCase = get(),
            ringtoneManager = get()
        )
    }
    viewModel { (selectedRingtone: NameAndUri) -> RingtoneListViewModel(selectedRingtone, get()) }
}