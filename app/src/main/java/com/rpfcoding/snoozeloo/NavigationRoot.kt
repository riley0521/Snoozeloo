package com.rpfcoding.snoozeloo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.rpfcoding.snoozeloo.feature_alarm.presentation.add_edit.AddEditAlarmAction
import com.rpfcoding.snoozeloo.feature_alarm.presentation.add_edit.AddEditAlarmScreenRoot
import com.rpfcoding.snoozeloo.feature_alarm.presentation.add_edit.AddEditAlarmViewModel
import com.rpfcoding.snoozeloo.feature_alarm.presentation.list.AlarmListScreenRoot
import com.rpfcoding.snoozeloo.feature_alarm.presentation.ringtone_list.RingtoneListScreenRoot
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavigationRoot(
    navController: NavHostController
) {

    NavHost(navController = navController, startDestination = "alarm") {
        alarmGraph(navController)
    }
}

private fun NavGraphBuilder.alarmGraph(navController: NavHostController) {
    navigation(
        route = "alarm",
        startDestination = "alarm_list"
    ) {
        composable(route = "alarm_list") { entry ->
            AlarmListScreenRoot(
                navigateToAddEditScreen = {
                    navController.navigate("alarm_detail/$it")
                }
            )
        }

        composable(
            route = "alarm_detail/{alarmId}",
            arguments = listOf(
                navArgument(
                    name = "alarmId"
                ) {
                    this.type = NavType.StringType
                    this.nullable = true
                }
            )
        ) { entry ->
            val alarmId = entry.arguments?.getString("alarmId")
            val viewModel = entry.sharedKoinViewModel<AddEditAlarmViewModel>(navController)

            AddEditAlarmScreenRoot(
                navigateBack = {
                    navController.navigateUp()
                    // We need to reset state because alarm_list is in the same navGraph.
                    // So, this viewModel will not be cleared.
                    viewModel.resetState()
                },
                navigateToRingtoneList = {
                    navController.navigate("alarm_ringtones")
                },
                alarmId = alarmId,
                viewModel = viewModel
            )
        }

        composable(route = "alarm_ringtones") { entry ->
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