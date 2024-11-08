package com.rpfcoding.snoozeloo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.rpfcoding.snoozeloo.ui.theme.SnoozelooTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        setContent {
            SnoozelooTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column {
                        Text(
                            text = "Your Alarms",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Wake up",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row {
                            Text(
                                text = "10:00",
                                style = MaterialTheme.typography.displayMedium
                            )
                            Text(
                                text = "AM",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Text(
                            text = "Alarm in 30min",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "It's empty! Add the first alarm so you don't miss an important moment!",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}