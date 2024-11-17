@file:OptIn(DelicateCoroutinesApi::class)

package com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rpfcoding.snoozeloo.feature_alarm.domain.AlarmRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootCompletedReceiver: BroadcastReceiver() {

    private val bootCompletedHelper by lazy { BootCompletedHelper() }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        bootCompletedHelper.onReceive(intent)
    }

    private class BootCompletedHelper: KoinComponent {
        private val alarmRepository: AlarmRepository by inject()

        fun onReceive(intent: Intent?) {
            if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
                GlobalScope.launch {
                    alarmRepository.scheduleAllEnabledAlarms()
                }
            }
        }
    }
}