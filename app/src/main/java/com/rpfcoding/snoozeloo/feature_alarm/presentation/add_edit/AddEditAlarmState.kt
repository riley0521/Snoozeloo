package com.rpfcoding.snoozeloo.feature_alarm.presentation.add_edit

data class AddEditAlarmState(
    val hour: String = "",
    val minute: String = "",
    val alarmName: String = "",
    val error: String? = null,
    val canSave: Boolean = false,
    val isSaving: Boolean = false
)
