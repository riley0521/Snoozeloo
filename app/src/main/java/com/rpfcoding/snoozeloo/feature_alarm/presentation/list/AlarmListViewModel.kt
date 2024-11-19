package com.rpfcoding.snoozeloo.feature_alarm.presentation.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmRepository
import com.rpfcoding.snoozeloo.feature_alarm.domain.DayValue
import com.rpfcoding.snoozeloo.feature_alarm.domain.GetFutureDateUseCase
import com.rpfcoding.snoozeloo.feature_alarm.domain.GetTimeLeftInSecondsUseCase
import com.rpfcoding.snoozeloo.feature_alarm.domain.GetTimeToSleepInSecondsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AlarmListViewModel(
    private val alarmRepository: AlarmRepository,
    private val getFutureDateUseCase: GetFutureDateUseCase,
    private val getTimeLeftInSecondsUseCase: GetTimeLeftInSecondsUseCase,
    private val getTimeToSleepInSecondsUseCase: GetTimeToSleepInSecondsUseCase
): ViewModel() {

    var state by mutableStateOf(AlarmListState())
        private set

    init {
        alarmRepository
            .getAll()
            .onEach { alarms ->
                state = state.copy(
                    alarms = alarms
                )
            }.launchIn(viewModelScope)
    }

    fun onAction(action: AlarmListAction) {
        when (action) {
            is AlarmListAction.OnToggleAlarm -> {
                viewModelScope.launch {
                    alarmRepository.toggle(action.alarm)
                }
            }
            is AlarmListAction.OnDeleteAlarmClick -> {
                viewModelScope.launch {
                    alarmRepository.deleteById(action.id)
                }
            }
            is AlarmListAction.OnToggleDayOfAlarm -> {
                viewModelScope.launch {
                    alarmRepository.toggleDay(action.day, action.alarm)
                }
            }
            else -> Unit
        }
    }

    fun getTimeLeftInSecondsFlow(hour: Int, minute: Int, repeatDays: Set<DayValue>): Flow<Long> {
        val futureDateTime = getFutureDateUseCase(hour, minute, repeatDays)
        return getTimeLeftInSecondsUseCase(futureDateTime)
    }

    fun getTimeToSleepInSecondsFlow(hour: Int, minute: Int, repeatDays: Set<DayValue>): Flow<Long?> {
        val futureDateTime = getFutureDateUseCase(hour, minute, repeatDays)
        return getTimeToSleepInSecondsUseCase(hour, futureDateTime)
    }
}