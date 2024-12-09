package com.rpfcoding.snoozeloo.feature_alarm.presentation.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpfcoding.snoozeloo.core.domain.ringtone.RingtoneManager
import com.rpfcoding.snoozeloo.core.util.formatNumberWithLeadingZero
import com.rpfcoding.snoozeloo.feature_alarm.domain.Alarm
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmRepository
import com.rpfcoding.snoozeloo.feature_alarm.domain.ValidateAlarmUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.roundToInt

class AddEditAlarmViewModel(
    private val alarmId: String?,
    private val alarmRepository: AlarmRepository,
    private val validateAlarmUseCase: ValidateAlarmUseCase,
    private val ringtoneManager: RingtoneManager
): ViewModel() {

    var state by mutableStateOf(AddEditAlarmState())
        private set

    private val hourFlow = snapshotFlow { state.hour }
    private val minuteFlow = snapshotFlow { state.minute }

    private val eventChannel = Channel<AddEditAlarmEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        combine(hourFlow, minuteFlow) { hour, minute ->
            val isValid = validateAlarmUseCase(hour, minute)
            state = state.copy(canSave = isValid)
        }.launchIn(viewModelScope)

        getExistingAlarm()
    }

    private fun getExistingAlarm() = viewModelScope.launch {
        val existingAlarm = alarmId?.let { alarmRepository.getById(it) } ?: run {
            setDefaultRingtone()
            return@launch
        }
        val ringtone = ringtoneManager
            .getAvailableRingtones().let { ringtones ->
                ringtones.firstOrNull { (_, uri) -> uri == existingAlarm.ringtoneUri }
                    ?: ringtones.getOrNull(1)
            }

        state = state.copy(
            alarmName = existingAlarm.name,
            hour = formatNumberWithLeadingZero(existingAlarm.hour),
            minute = formatNumberWithLeadingZero(existingAlarm.minute),
            repeatDays = existingAlarm.repeatDays,
            ringtone = ringtone,
            volume = (existingAlarm.volume / 100f),
            vibrate = existingAlarm.vibrate
        )
    }

    private suspend fun setDefaultRingtone() {
        val ringtone = ringtoneManager.getAvailableRingtones().getOrNull(1)

        state = state.copy(
            ringtone = ringtone
        )
    }

    fun onAction(action: AddEditAlarmAction) {
        when (action) {
            is AddEditAlarmAction.OnEditAlarmNameTextChange -> {
                state = state.copy(alarmName = action.value)
            }
            is AddEditAlarmAction.OnHourTextChange -> {
                state = state.copy(hour = action.value)
            }
            is AddEditAlarmAction.OnMinuteTextChange -> {
                state = state.copy(minute = action.value)
            }
            is AddEditAlarmAction.OnDayChipToggle -> {
                val mutableRepeatDays = state.repeatDays.toMutableSet()
                if (mutableRepeatDays.contains(action.value)) {
                    mutableRepeatDays.remove(action.value)
                } else {
                    mutableRepeatDays.add(action.value)
                }

                state = state.copy(repeatDays = mutableRepeatDays)
            }
            is AddEditAlarmAction.OnVolumeChange -> {
                state = state.copy(volume = action.value)
            }
            AddEditAlarmAction.OnVibrateToggle -> {
                state = state.copy(vibrate = !state.vibrate)
            }
            is AddEditAlarmAction.OnAlarmRingtoneChange -> {
                state = state.copy(ringtone = action.value)
            }
            AddEditAlarmAction.OnAddEditAlarmNameClick -> {
                state = state.copy(isDialogOpened = true)
            }
            AddEditAlarmAction.OnCloseEditAlarmNameDialogClick -> {
                state = state.copy(isDialogOpened = false)
            }
            AddEditAlarmAction.OnSaveClick -> {
                viewModelScope.launch {
                    val updatedAlarm = Alarm(
                        id = alarmId ?: UUID.randomUUID().toString(),
                        name = if (state.alarmName.isBlank()) "" else state.alarmName.trim(),
                        hour = state.hour.toIntOrNull() ?: 0,
                        minute = state.minute.toIntOrNull() ?: 0,
                        enabled = true,
                        repeatDays = state.repeatDays,
                        volume = (state.volume * 100).roundToInt().coerceAtMost(100),
                        ringtoneUri = state.ringtone?.second.orEmpty(),
                        vibrate = state.vibrate
                    )
                    alarmRepository.upsert(updatedAlarm)
                    eventChannel.send(AddEditAlarmEvent.OnSuccess)
                }
            }
            else -> Unit
        }
    }
}