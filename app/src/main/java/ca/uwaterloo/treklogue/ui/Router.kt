package ca.uwaterloo.treklogue.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ca.uwaterloo.treklogue.R
import ca.uwaterloo.treklogue.controller.UserController
import ca.uwaterloo.treklogue.ui.composables.ScaffoldBottom
import ca.uwaterloo.treklogue.ui.composables.ScaffoldTop
import ca.uwaterloo.treklogue.ui.login.LoginViewModel
import ca.uwaterloo.treklogue.ui.map.MapScreen
import ca.uwaterloo.treklogue.ui.profile.ProfileScreen
import ca.uwaterloo.treklogue.ui.settings.SettingsScreen

/**
 * enum values that represent the screens in the app
 * maybe move this into data?
 */
enum class Screen(@StringRes val title: Int) {
    Map(title = R.string.home_name),
    Profile(title = R.string.profile_name),
    Settings(title = R.string.settings_name),
}

// possibly move this
enum class ViewEvent {
    UsernameEvent,
    PasswordEvent,
    ToggleEvent,
}

@Composable
fun Router(
    loginViewModel: LoginViewModel,
    userViewModel: UserViewModel,
    userController: UserController
) {
    val navController: NavHostController = rememberNavController()
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = Screen.valueOf(
        // default should probably be login once that's made
        backStackEntry?.destination?.route ?: Screen.Map.name
    )

    // move this somewhere more easily accessible?
    val viewModel by remember { mutableStateOf(userViewModel) }
    val controller by remember { mutableStateOf(userController) }

    Scaffold(
        topBar = {
            ScaffoldTop(
                // may be good to wrap the home/default in a val
                navigateHome = { navController.navigate(Screen.Map.name) },
                navigateUp = { navController.navigateUp() },
                canNavigateBack = currentScreen == Screen.Settings
            )
        },
        bottomBar = {
            ScaffoldBottom(
                currentScreen = currentScreen,
                navigateHome = { navController.navigate(Screen.Map.name) },
                navigateProfile = { navController.navigate(Screen.Profile.name) },
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Map.name,
            modifier = Modifier
                .fillMaxSize()

                //This messes up the scrolling for google maps
                //.verticalScroll(rememberScrollState())

                .padding(innerPadding)
        ) {
            // add routes here
            composable(route = Screen.Map.name) {
                MapScreen(
                    onNextButtonClicked = {
                        navController.navigate(Screen.Profile.name)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = Screen.Profile.name) {
                ProfileScreen(
                    onNextButtonClicked = {
                        navController.navigate(Screen.Settings.name)
                    },
                    modifier = Modifier.fillMaxSize(),
                    loginViewModel
                )
            }
            composable(route = Screen.Settings.name) {
                SettingsScreen(
                    Modifier, viewModel, controller
                )
            }
        }
    }
}