package com.rpfcoding.snoozeloo

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import com.rpfcoding.snoozeloo.core.presentation.designsystem.SnoozelooTheme
import com.rpfcoding.snoozeloo.core.util.isTiramisuPlus

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()

        if (isTiramisuPlus()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }

        setContent {
            SnoozelooTheme {
                val navController = rememberNavController()
                NavigationRoot(
                    navController = navController
                )
            }
        }
    }
}