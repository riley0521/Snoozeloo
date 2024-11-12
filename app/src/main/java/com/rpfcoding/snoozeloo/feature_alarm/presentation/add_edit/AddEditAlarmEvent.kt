package com.rpfcoding.snoozeloo.feature_alarm.presentation.add_edit

import com.rpfcoding.snoozeloo.core.presentation.ui.UiText

interface AddEditAlarmEvent {
    data object OnSuccess: AddEditAlarmEvent
    data class OnFailure(val uiText: UiText): AddEditAlarmEvent
}