package com.rpfcoding.snoozeloo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.rpfcoding.snoozeloo.core.domain.ringtone.NameAndUri
import com.rpfcoding.snoozeloo.feature_alarm.presentation.add_edit.AddEditAlarmAction
import com.rpfcoding.snoozeloo.feature_alarm.presentation.add_edit.AddEditAlarmScreenRoot
import com.rpfcoding.snoozeloo.feature_alarm.presentation.add_edit.AddEditAlarmViewModel
import com.rpfcoding.snoozeloo.feature_alarm.presentation.list.AlarmListScreenRoot
import com.rpfcoding.snoozeloo.feature_alarm.presentation.nav.AlarmGraph
import com.rpfcoding.snoozeloo.feature_alarm.presentation.ringtone_list.RingtoneListScreenRoot
import com.rpfcoding.snoozeloo.feature_alarm.presentation.ringtone_list.RingtoneListViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NavigationRoot(
    navController: NavHostController
) {

    NavHost(navController = navController, startDestination = AlarmGraph.Root) {
        alarmGraph(navController)
    }
}

private fun NavGraphBuilder.alarmGraph(navController: NavHostController) {
    navigation<AlarmGraph.Root>(
        startDestination = AlarmGraph.AlarmList
    ) {
        composable<AlarmGraph.AlarmList> {
            AlarmListScreenRoot(
                navigateToAddEditScreen = {
                    navController.navigate(AlarmGraph.AlarmDetail(it))
                }
            )
        }

        composable<AlarmGraph.AlarmDetail> { entry ->
            val alarmDetailRoute: AlarmGraph.AlarmDetail = entry.toRoute()
            val viewModel: AddEditAlarmViewModel = koinViewModel { parametersOf(alarmDetailRoute.alarmId) }

            LaunchedEffect(Unit) {
                val nameAndUri = entry.savedStateHandle.get<NameAndUri>("selectedRingtone") ?: return@LaunchedEffect
                viewModel.onAction(AddEditAlarmAction.OnAlarmRingtoneChange(nameAndUri))
            }

            AddEditAlarmScreenRoot(
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToRingtoneList = {
                    val (name, uri) = viewModel.state.ringtone ?: Pair(null, null)
                    navController.navigate(AlarmGraph.RingtoneList(name, uri))
                },
                viewModel = viewModel
            )
        }

        composable<AlarmGraph.RingtoneList> { entry ->
            val ringtoneListRoute: AlarmGraph.RingtoneList = entry.toRoute()
            val viewModel: RingtoneListViewModel = koinViewModel { parametersOf(ringtoneListRoute.getNameAndUri()) }

            RingtoneListScreenRoot(
                onRingtoneSelected = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("selectedRingtone", it)
                },
                navigateBack = {
                    navController.navigateUp()
                },
                viewModel = viewModel
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