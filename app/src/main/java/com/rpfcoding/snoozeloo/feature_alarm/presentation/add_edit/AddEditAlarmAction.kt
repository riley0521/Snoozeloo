package com.rpfcoding.snoozeloo.feature_alarm.presentation.add_edit

sealed interface AddEditAlarmAction {
    data object OnCloseClick: AddEditAlarmAction
    data object OnSaveClick: AddEditAlarmAction
    data class OnHourTextChange(val value: String): AddEditAlarmAction
    data class OnMinuteTextChange(val value: String): AddEditAlarmAction
    data class OnEditAlarmNameTextChange(val value: String): AddEditAlarmAction
    data object OnAddEditAlarmNameClick: AddEditAlarmAction
}