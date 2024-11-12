package com.rpfcoding.snoozeloo.feature_alarm.scheduler_receiver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rpfcoding.snoozeloo.R
import com.rpfcoding.snoozeloo.core.presentation.designsystem.SnoozelooTheme
import com.rpfcoding.snoozeloo.core.util.formatHourMinute
import com.rpfcoding.snoozeloo.feature_alarm.domain.Alarm

@Composable
fun AlarmTriggerScreen(
    alarm: Alarm,
    onTurnOffClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.alarm),
            contentDescription = null,
            modifier = Modifier.size(55.5.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = formatHourMinute(alarm.hour, alarm.minute),
            style = MaterialTheme.typography.displayMedium,
            fontSize = 82.sp,
            lineHeight = 99.9.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = alarm.name.uppercase(),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 24.sp,
            lineHeight = 29.2.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onTurnOffClick,
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(
                text = "Turn Off",
                modifier = Modifier.padding(
                    vertical = 8.dp,
                    horizontal = 32.dp
                ),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 24.sp,
                lineHeight = 29.2.sp
            )
        }
    }
}

@Preview
@Composable
private fun AlarmTriggerScreenPreview() {
    SnoozelooTheme {
        AlarmTriggerScreen(
            alarm = Alarm(name = "Work", hour = 10, minute = 0, enabled = true, ringtoneUri = ""),
            onTurnOffClick = {}
        )
    }
}