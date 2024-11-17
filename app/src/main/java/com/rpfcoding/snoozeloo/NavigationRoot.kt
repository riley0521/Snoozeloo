package com.rpfcoding.snoozeloo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rpfcoding.snoozeloo.feature_alarm.presentation.add_edit.AddEditAlarmAction
import com.rpfcoding.snoozeloo.feature_alarm.presentation.add_edit.AddEditAlarmScreenRoot
import com.rpfcoding.snoozeloo.feature_alarm.presentation.add_edit.AddEditAlarmViewModel
import com.rpfcoding.snoozeloo.feature_alarm.presentation.list.AlarmListScreenRoot
import com.rpfcoding.snoozeloo.feature_alarm.presentation.ringtone_list.RingtoneListScreenRoot
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavigationRoot(
    navController: NavHostController
) {

    NavHost(navController = navController, startDestination = AlarmGraph.AlarmRoot) {
        alarmGraph(navController)
    }
}

object AlarmGraph {
    @Serializable
    data object AlarmRoot

    @Serializable
    data object AlarmList

    @Serializable
    data class AddEditAlarm(val alarmId: String? = null)

    @Serializable
    data object RingtoneList
}

private fun NavGraphBuilder.alarmGraph(navController: NavHostController) {
    navigation<AlarmGraph.AlarmRoot>(
        startDestination = AlarmGraph.AlarmList
    ) {
        composable<AlarmGraph.AlarmList> {
            AlarmListScreenRoot(
                navigateToAddEditScreen = {
                    navController.navigate(AlarmGraph.AddEditAlarm(alarmId = it))
                }
            )
        }

        composable<AlarmGraph.AddEditAlarm> { entry ->
            val viewModel = entry.sharedKoinViewModel<AddEditAlarmViewModel>(navController)

            AddEditAlarmScreenRoot(
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToRingtoneList = {
                    navController.navigate(AlarmGraph.RingtoneList)
                },
                viewModel = viewModel
            )
        }

        composable<AlarmGraph.RingtoneList> { entry ->
            val viewModel = entry.sharedKoinViewModel<AddEditAlarmViewModel>(navController)

            RingtoneListScreenRoot(
                selectedRingtone = viewModel.state.ringtone,
                onRingtoneSelected = {
                    viewModel.onAction(AddEditAlarmAction.OnAlarmRingtoneChange(it))
                },
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}

@Composable
inline fun <reified T: ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavHostController
): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel(viewModelStoreOwner = parentEntry)
}