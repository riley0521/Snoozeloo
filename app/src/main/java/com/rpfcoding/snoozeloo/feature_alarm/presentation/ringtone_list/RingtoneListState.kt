package com.rpfcoding.snoozeloo.feature_alarm.presentation.ringtone_list

import com.rpfcoding.snoozeloo.core.domain.ringtone.NameAndUri

data class RingtoneListState(
    val ringtones: List<NameAndUri> = emptyList()
)
