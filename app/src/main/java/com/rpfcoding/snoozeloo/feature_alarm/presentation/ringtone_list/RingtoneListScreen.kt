package com.rpfcoding.snoozeloo.feature_alarm.presentation.ringtone_list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rpfcoding.snoozeloo.R
import com.rpfcoding.snoozeloo.core.domain.ringtone.NameAndUri
import com.rpfcoding.snoozeloo.core.domain.ringtone.SILENT
import com.rpfcoding.snoozeloo.core.presentation.designsystem.SnoozelooTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun RingtoneListScreenRoot(
    onRingtoneSelected: (NameAndUri) -> Unit,
    navigateBack: () -> Unit,
    viewModel: RingtoneListViewModel = koinViewModel()
) {
    val state = viewModel.state

    BackHandler {
        viewModel.onAction(RingtoneListAction.OnBackClick)
        navigateBack()
    }

    RingtoneListScreen(
        ringtones = state.ringtones,
        selectedRingtone = state.selectedRingtone,
        onRingtoneSelected = {
            viewModel.onAction(RingtoneListAction.OnRingtoneSelected(it))
            onRingtoneSelected(it)
        },
        onBackClick = {
            viewModel.onAction(RingtoneListAction.OnBackClick)
            navigateBack()
        }
    )
}

@Composable
private fun RingtoneListScreen(
    ringtones: List<NameAndUri>,
    selectedRingtone: NameAndUri?,
    onRingtoneSelected: (NameAndUri) -> Unit,
    onBackClick: () -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        item {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.4.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(
                        role = Role.Button
                    ) {
                        onBackClick()
                    },
                tint = MaterialTheme.colorScheme.background
            )
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
        items(ringtones) { (name, uri) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(
                        vertical = 10.dp,
                        horizontal = 16.dp
                    )
                    .clickable {
                        onRingtoneSelected(Pair(name, uri))
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = if (uri == SILENT) {
                        painterResource(R.drawable.ic_silent)
                    } else {
                        painterResource(R.drawable.ic_ringtone)
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(6.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                if (selectedRingtone == Pair(name, uri)) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(4.dp),
                        tint = MaterialTheme.colorScheme.background
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Preview
@Composable
private fun RingtoneListScreenPreview() {
    SnoozelooTheme {
        RingtoneListScreen(
            ringtones = listOf(
                Pair("Silent", SILENT),
                Pair("Default (Bright Morning)", "default")
            ),
            selectedRingtone = Pair("Default (Bright Morning)", "default"),
            onRingtoneSelected = {},
            onBackClick = {}
        )
    }
}