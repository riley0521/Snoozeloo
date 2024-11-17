package com.rpfcoding.snoozeloo.core.ringtone.di

import com.rpfcoding.snoozeloo.core.domain.ringtone.RingtoneManager
import com.rpfcoding.snoozeloo.core.ringtone.AndroidRingtoneManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

val coreRingtoneModule = module {
    single { AndroidRingtoneManager(androidContext()) }.bind<RingtoneManager>()
}