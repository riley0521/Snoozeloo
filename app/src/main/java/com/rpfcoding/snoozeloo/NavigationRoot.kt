package com.rpfcoding.snoozeloo

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rpfcoding.snoozeloo.feature_alarm.presentation.add_edit.AddEditAlarmScreenRoot
import com.rpfcoding.snoozeloo.feature_alarm.presentation.list.AlarmListScreenRoot
import kotlinx.serialization.Serializable

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

        composable<AlarmGraph.AddEditAlarm> {
            AddEditAlarmScreenRoot(
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}