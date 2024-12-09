package com.rpfcoding.snoozeloo.feature_alarm.presentation.add_edit

import com.rpfcoding.snoozeloo.core.domain.ringtone.NameAndUri
import com.rpfcoding.snoozeloo.feature_alarm.domain.DayValue

sealed interface AddEditAlarmAction {
    data object OnCloseClick: AddEditAlarmAction
    data object OnSaveClick: AddEditAlarmAction
    data class OnHourTextChange(val value: String): AddEditAlarmAction
    data class OnMinuteTextChange(val value: String): AddEditAlarmAction
    data class OnEditAlarmNameTextChange(val value: String): AddEditAlarmAction
    data object OnAddEditAlarmNameClick: AddEditAlarmAction
    data object OnCloseEditAlarmNameDialogClick: AddEditAlarmAction
    data class OnDayChipToggle(val value: DayValue): AddEditAlarmAction
    data object OnAlarmRingtoneClick: AddEditAlarmAction
    data class OnAlarmRingtoneChange(val value: NameAndUri): AddEditAlarmAction
    data class OnVolumeChange(val value: Float): AddEditAlarmAction
    data object OnVibrateToggle: AddEditAlarmAction
}