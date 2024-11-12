package com.rpfcoding.snoozeloo.core.util

import android.os.Build

fun isTiramisuPlus(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}

fun isPiePlus(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
}

fun isOreoPlus(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
}

fun isOreoMr1Plus(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
}