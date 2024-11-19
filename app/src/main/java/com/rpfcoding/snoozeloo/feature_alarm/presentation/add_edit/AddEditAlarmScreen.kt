@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.rpfcoding.snoozeloo.feature_alarm.presentation.add_edit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rpfcoding.snoozeloo.R
import com.rpfcoding.snoozeloo.core.components.InputTimeTextField
import com.rpfcoding.snoozeloo.core.presentation.designsystem.SnoozelooTheme
import com.rpfcoding.snoozeloo.core.util.showToast
import com.rpfcoding.snoozeloo.feature_alarm.domain.DayValue
import com.rpfcoding.snoozeloo.feature_alarm.presentation.components.DayChip
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddEditAlarmScreenRoot(
    navigateBack: () -> Unit,
    navigateToRingtoneList: () -> Unit,
    alarmId: String?,
    viewModel: AddEditAlarmViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val state = viewModel.state
    var isNavigatingBack by remember { mutableStateOf(false) }

    BackHandler {}

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                AddEditAlarmEvent.OnSuccess -> {
                    isNavigatingBack = true
                    navigateBack()
                }
                is AddEditAlarmEvent.OnFailure -> {
                    context.showToast(event.uiText.asString(context))
                }
            }
        }
        isNavigatingBack = false
    }

    LaunchedEffect(state.existingAlarmFetched) {
        if (!state.existingAlarmFetched && !isNavigatingBack) {
            // Set the state with existingAlarm data.
            viewModel.getExistingAlarm(alarmId)

            // Acknowledge, so this block won't re-trigger again if the user comes back from RingtoneListScreen.
            viewModel.onAction(AddEditAlarmAction.AcknowledgeExistingAlarm)
        }
    }

    AddAlarmScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                AddEditAlarmAction.OnCloseClick -> {
                    isNavigatingBack = true
                    navigateBack()
                }
                AddEditAlarmAction.OnAlarmRingtoneClick -> navigateToRingtoneList()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
private fun AddAlarmScreen(
    state: AddEditAlarmState,
    onAction: (AddEditAlarmAction) -> Unit
) {
    var isDialogOpened by remember { mutableStateOf(false) }

    AddAlarmScreenMainContent(
        state = state,
        onAction = { action ->
            if (action is AddEditAlarmAction.OnAddEditAlarmNameClick) {
                isDialogOpened = true
            } else {
                onAction(action)
            }
        }
    )

    if (isDialogOpened) {
        Dialog(
            onDismissRequest = {
                isDialogOpened = false
            }
        ) {
            AddAlarmNameDialogContent(
                alarmName = state.alarmName,
                onValueChange = {
                    onAction(AddEditAlarmAction.OnEditAlarmNameTextChange(it))
                },
                onSaveClick = {
                    isDialogOpened = false
                }
            )
        }
    }
}

@Composable
private fun AddAlarmNameDialogContent(
    alarmName: String,
    onValueChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Alarm Name",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = alarmName,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.labelMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Button(
                onClick = onSaveClick,
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 6.dp
                ),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    disabledContentColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = "Save",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview
@Composable
private fun AddAlarmNameDialogContentPreview() {
    SnoozelooTheme {
        AddAlarmNameDialogContent(
            alarmName = "",
            onValueChange = {},
            onSaveClick = {}
        )
    }
}

@Composable
private fun AddAlarmScreenMainContent(
    state: AddEditAlarmState,
    onAction: (AddEditAlarmAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CloseAndSaveButtons(
            canSave = state.canSave,
            onCloseClick = {
                onAction(AddEditAlarmAction.OnCloseClick)
            },
            onSaveClick = {
                onAction(AddEditAlarmAction.OnSaveClick)
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        HourAndMinuteInputField(
            hour = state.hour,
            minute = state.minute,
            onHourChange = {
                onAction(AddEditAlarmAction.OnHourTextChange(it))
            },
            onMinuteChange = {
                onAction(AddEditAlarmAction.OnMinuteTextChange(it))
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        AlarmNameSection(
            alarmName = state.alarmName,
            onAddAlarmNameClick = {
                onAction(AddEditAlarmAction.OnAddEditAlarmNameClick)
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        AlarmRepeatDaysSection(
            repeatDays = state.repeatDays,
            onToggleDayChip = {
                onAction(AddEditAlarmAction.OnDayChipToggle(it))
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Alarm ringtone",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = state.ringtone?.first.orEmpty(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onAction(AddEditAlarmAction.OnAlarmRingtoneClick)
                    },
                textAlign = TextAlign.End
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                text = "Alarm volume",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = state.volume,
                onValueChange = {
                    onAction(AddEditAlarmAction.OnVolumeChange(it))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Vibrate",
                style = MaterialTheme.typography.bodyMedium
            )
            Switch(
                checked = state.vibrate,
                onCheckedChange = {
                    onAction(AddEditAlarmAction.OnVibrateToggle)
                }
            )
        }
    }
}

@Composable
private fun AlarmRepeatDaysSection(
    repeatDays: Set<DayValue>,
    onToggleDayChip: (DayValue) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Repeat",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(10.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            DayValue.entries.forEach { day ->
                DayChip(
                    dayValue = day,
                    isSelected = repeatDays.contains(day),
                    onClick = {
                        onToggleDayChip(day)
                    }
                )
            }
        }
    }
}

@Composable
private fun CloseAndSaveButtons(
    canSave: Boolean,
    onCloseClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.subtract),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clickable(
                    role = Role.Button
                ) {
                    onCloseClick()
                },
            tint = MaterialTheme.colorScheme.primary
        )
        Button(
            onClick = onSaveClick,
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 6.dp
            ),
            shape = RoundedCornerShape(30.dp),
            enabled = canSave,
            colors = ButtonDefaults.buttonColors(
                disabledContentColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Text(
                text = "Save",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun HourAndMinuteInputField(
    hour: String,
    minute: String,
    onHourChange: (String) -> Unit,
    onMinuteChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InputTimeTextField(
            value = hour,
            onValueChange = onHourChange,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = ":",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        InputTimeTextField(
            value = minute,
            onValueChange = onMinuteChange,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AlarmNameSection(
    alarmName: String,
    onAddAlarmNameClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .clickable(
                role = Role.Button
            ) {
                onAddAlarmNameClick()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Alarm Name",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = alarmName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun AddAlarmScreenPreview() {
    SnoozelooTheme {
        var state by remember { mutableStateOf(AddEditAlarmState(alarmName = "Work")) }

        AddAlarmScreenMainContent(
            state = state,
            onAction = { action ->
                when (action) {
                    is AddEditAlarmAction.OnHourTextChange -> {
                        state = state.copy(hour = action.value.take(2))
                    }
                    is AddEditAlarmAction.OnMinuteTextChange -> {
                        state = state.copy(minute = action.value.take(2))
                    }
                    else -> Unit
                }
            }
        )
    }
}