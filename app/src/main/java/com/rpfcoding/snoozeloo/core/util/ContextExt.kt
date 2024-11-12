package com.rpfcoding.snoozeloo.core.util

import android.app.NotificationManager
import android.content.Context
import android.os.PowerManager
import android.widget.Toast

fun Context.isScreenOn(): Boolean {
    return getSystemService(PowerManager::class.java).isInteractive
}

fun Context.hideNotification(id: Int) {
    val notificationManager = getSystemService(NotificationManager::class.java)
    notificationManager.cancel(id)
}

fun Context.showToast(value: String) {
    Toast.makeText(this, value, Toast.LENGTH_LONG).show()
}