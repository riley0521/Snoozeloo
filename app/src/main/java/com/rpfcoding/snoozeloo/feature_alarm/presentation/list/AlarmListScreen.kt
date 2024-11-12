package com.rpfcoding.snoozeloo.feature_alarm.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rpfcoding.snoozeloo.R
import com.rpfcoding.snoozeloo.core.presentation.designsystem.SnoozelooTheme
import com.rpfcoding.snoozeloo.core.util.formatHourMinute
import com.rpfcoding.snoozeloo.core.util.formatSeconds
import com.rpfcoding.snoozeloo.feature_alarm.domain.Alarm
import com.rpfcoding.snoozeloo.feature_alarm.domain.GetCurrentAndFutureDateUseCase
import com.rpfcoding.snoozeloo.feature_alarm.domain.convertLocalDateTimeToEpochSeconds
import com.rpfcoding.snoozeloo.feature_alarm.presentation.util.getDummyAlarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.androidx.compose.koinViewModel

@Composable
fun AlarmListScreenRoot(
    navigateToAddEditScreen: (alarmId: String?) -> Unit,
    viewModel: AlarmListViewModel = koinViewModel()
) {

    AlarmListScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                AlarmListAction.OnAddNewAlarmClick -> navigateToAddEditScreen(null)
                is AlarmListAction.OnAlarmClick -> navigateToAddEditScreen(action.id)
                else -> {
                    viewModel.onAction(action)
                }
            }
        },
        onGetTimeLeftInSeconds = { hour, minute ->
            viewModel.getTimeLeftInSecondsFlow(hour, minute)
        }
    )
}

@Composable
private fun AlarmListScreen(
    state: AlarmListState,
    onAction: (AlarmListAction) -> Unit,
    onGetTimeLeftInSeconds: (hour: Int, minute: Int) -> Flow<Long>,
    isPreview: Boolean = false
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAction(AlarmListAction.OnAddNewAlarmClick)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .padding(bottom = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(38.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        if (state.alarms.isEmpty()) {
            EmptyAlarmListContent(
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(padding),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp
                )
            ) {
                item {
                    Text(
                        text = "Your Alarms",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
                items(state.alarms, key = { it.id }) { alarm ->
                    val timeLeftInSeconds by onGetTimeLeftInSeconds(alarm.hour, alarm.minute).collectAsState(0L)

                    AlarmListItem(
                        alarm = alarm,
                        timeLeftInSeconds = if (isPreview) {
                            getTimeLeftInSeconds(alarm)
                        } else timeLeftInSeconds,
                        onAlarmClick = {
                            onAction(AlarmListAction.OnAlarmClick(alarm.id))
                        },
                        onDeleteAlarmClick = {
                            onAction(AlarmListAction.OnDeleteAlarmClick(alarm.id))
                        },
                        onToggleAlarm = {
                            onAction(AlarmListAction.OnToggleAlarm(alarm))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyAlarmListContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = "Your Alarms",
            style = MaterialTheme.typography.titleLarge
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.alarm),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(62.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "It's empty! Add the first alarm so you don't miss an important moment!",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AlarmListItem(
    alarm: Alarm,
    timeLeftInSeconds: Long,
    onAlarmClick: () -> Unit,
    onDeleteAlarmClick: () -> Unit,
    onToggleAlarm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = alarm.name.ifBlank { "Alarm" },
                style = MaterialTheme.typography.bodyMedium
            )
            Switch(
                checked = alarm.enabled,
                onCheckedChange = {
                    onToggleAlarm()
                }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = formatHourMinute(alarm.hourTwelve, alarm.minute),
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier
                    .alignByBaseline()
                    .clickable(
                        role = Role.Button
                    ) {
                        onAlarmClick()
                    }
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (alarm.isMorning) "AM" else "PM",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.alignByBaseline()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (alarm.enabled) {
            val remainingTimeStr = formatSeconds(timeLeftInSeconds)

            if (remainingTimeStr.isNotBlank()) {
                Text(
                    text = "Alarm in $remainingTimeStr",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        IconButton(
            onClick = onDeleteAlarmClick,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = Color.Red
            )
        }
    }
}

@Preview
@Composable
private fun AlarmListScreenPreview() {
    SnoozelooTheme {
        AlarmListScreen(
            state = AlarmListState(
                alarms = getDummyAlarms()
            ),
            onAction = {},
            onGetTimeLeftInSeconds = { _, _ ->
                emptyFlow()
            },
            isPreview = true
        )
    }
}

@Preview
@Composable
private fun EmptyAlarmListContentPreview() {
    SnoozelooTheme {
        EmptyAlarmListContent(
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Preview
@Composable
private fun AlarmListItemPreview() {
    SnoozelooTheme {
        val alarm = getDummyAlarms()[0]

        AlarmListItem(
            alarm = alarm,
            timeLeftInSeconds = getTimeLeftInSeconds(alarm),
            onAlarmClick = {},
            onDeleteAlarmClick = {},
            onToggleAlarm = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun getTimeLeftInSeconds(alarm: Alarm): Long {
    val (curDateTime, futureDateTime) = GetCurrentAndFutureDateUseCase().invoke(alarm.hour, alarm.minute)
    return convertLocalDateTimeToEpochSeconds(futureDateTime) - convertLocalDateTimeToEpochSeconds(curDateTime)
}

private fun getDummyAlarms() = listOf(
    getDummyAlarm(name = "Wake Up", hour = 10, minute = 0, enabled = true),
    getDummyAlarm(name = "Education", hour = 16, minute = 30, enabled = true),
    getDummyAlarm(name = "Dinner", hour = 18, minute = 45, enabled = false)
)