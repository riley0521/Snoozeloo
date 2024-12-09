package com.rpfcoding.snoozeloo.feature_alarm.presentation.ringtone_list

import com.rpfcoding.snoozeloo.core.domain.ringtone.NameAndUri

sealed interface RingtoneListAction {
    data class OnRingtoneSelected(val ringtone: NameAndUri): RingtoneListAction
    data object OnBackClick: RingtoneListAction
}