package com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpfcoding.snoozeloo.feature_alarm.domain.Alarm
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmRepository
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val alarmRepository: AlarmRepository
): ViewModel() {

    var alarm by mutableStateOf<Alarm?>(null)
        private set

    fun getAlarmById(id: String) = viewModelScope.launch {
        alarm = alarmRepository.getById(id)
    }

    fun disableAlarm(id: String) = viewModelScope.launch {
        alarmRepository.disableAlarmById(id)
    }

    fun rescheduleAlarm() = viewModelScope.launch {
        alarm?.let {
            // Here we don't need to disable the alarm first since we're not in AlarmListScreen.
            // This activity will only trigger if the device is asleep.
            // So, we can just do it like this.
            alarmRepository.upsert(it.copy(enabled = true))
        }
    }
}