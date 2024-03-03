package ca.uwaterloo.treklogue.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ca.uwaterloo.treklogue.ui.composables.JournalEntryUI
import ca.uwaterloo.treklogue.ui.composables.ScaffoldBottom
import ca.uwaterloo.treklogue.ui.composables.Screens
import ca.uwaterloo.treklogue.ui.list.ListScreen
import ca.uwaterloo.treklogue.ui.list.JournalEntryDetail
import ca.uwaterloo.treklogue.ui.list.ListViewModel
import ca.uwaterloo.treklogue.ui.map.MapScreen
import ca.uwaterloo.treklogue.ui.map.MapViewModel
import ca.uwaterloo.treklogue.ui.profile.ProfileScreen
import ca.uwaterloo.treklogue.ui.settings.SettingsScreen

@Composable
fun Router(
    userViewModel: UserViewModel,
    mapViewModel: MapViewModel,
    listViewModel: ListViewModel
) {
    val navigationController = rememberNavController()
    val selectedTab = remember {
        mutableStateOf(Icons.Default.LocationOn)
    }

    Scaffold(
        /*
        topBar = {
            ScaffoldTop(
                // may be good to wrap the home/default in a val
                navigateHome = { navController.navigate(Screen.Map.name) },
                navigateUp = { navController.navigateUp() },
                canNavigateBack = currentScreen == Screen.Settings
            )
        },
         */
        bottomBar = {
            ScaffoldBottom(
                selectedTab = selectedTab,
                navigationController = navigationController
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navigationController,
            startDestination = Screens.Map.screen,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = Screens.Map.screen) {
                MapScreen(
                    modifier = Modifier.fillMaxSize(),
                    mapViewModel,
                    onDetailClicked = { _ ->
                        navigationController.navigate(Screens.JournalEntry.screen)
                    },
                )
            }

            composable(route = Screens.List.screen) {
                ListScreen(
                    onDetailClicked = {
                        navigationController.navigate(Screens.JournalDetail.screen)
                    },
                    modifier = Modifier.fillMaxSize(),
                    listViewModel
                )
            }

            composable(route = Screens.JournalDetail.screen) {
                JournalEntryDetail(
                    onBackClicked = {
                        navigationController.navigate(Screens.List.screen)
                    },
                    modifier = Modifier.fillMaxSize(),
                    listViewModel
                )
            }

            composable(route = Screens.Profile.screen) {
                ProfileScreen(
                    modifier = Modifier.fillMaxSize(),
                    userViewModel
                )
            }
            composable(route = Screens.Settings.screen) {
                SettingsScreen(
                    Modifier, userViewModel
                )
            }
            composable(route = Screens.JournalEntry.screen) {
                JournalEntryUI(
                    onDetailClicked = { _ ->
                        navigationController.navigate(Screens.Map.screen)
                    },
                )
            }
        }
    }
}