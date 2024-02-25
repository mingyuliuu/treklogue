@file:OptIn(ExperimentalMaterial3Api::class)

package ca.uwaterloo.treklogue

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.lifecycleScope
import ca.uwaterloo.treklogue.controller.UserController
import ca.uwaterloo.treklogue.data.model.User
import ca.uwaterloo.treklogue.data.model.UserModel
import ca.uwaterloo.treklogue.data.repository.BadgeRealmSyncRepository
import ca.uwaterloo.treklogue.data.repository.JournalEntryRealmSyncRepository
import ca.uwaterloo.treklogue.data.repository.LandmarkRealmSyncRepository
import ca.uwaterloo.treklogue.data.repository.UserRealmSyncRepository
import ca.uwaterloo.treklogue.ui.Router
import ca.uwaterloo.treklogue.ui.UserViewModel
import ca.uwaterloo.treklogue.ui.login.LoginActivity
import ca.uwaterloo.treklogue.ui.login.LoginEvent
import ca.uwaterloo.treklogue.ui.login.LoginViewModel
import ca.uwaterloo.treklogue.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val landmarkRepository = LandmarkRealmSyncRepository { _, error ->
        // Sync errors come from a background thread so route the Toast through the UI thread
        lifecycleScope.launch {
            // Catch write permission errors and notify user. This is just a 2nd line of defense
            // since we prevent users from modifying someone else's tasks
            // TODO the SDK does not have an enum for this type of error yet so make sure to update this once it has been added
            if (error.message?.contains("CompensatingWrite") == true) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.permissions_error),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private val badgeRepository = BadgeRealmSyncRepository { _, error ->
        lifecycleScope.launch {
            if (error.message?.contains("CompensatingWrite") == true) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.permissions_error),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private val journalEntryRepository = JournalEntryRealmSyncRepository { _, error ->
        lifecycleScope.launch {
            if (error.message?.contains("CompensatingWrite") == true) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.permissions_error),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private val userRepository = UserRealmSyncRepository { _, error ->
        lifecycleScope.launch {
            if (error.message?.contains("CompensatingWrite") == true) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.permissions_error),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            // Subscribe to navigation and message-logging events
            loginViewModel.event
                .collect { event ->
                    when (event) {
                        is LoginEvent.LogOutAndExit -> {
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        else -> {
                            // SKIP
                        }
                    }
                }
        }

        // TODO: fix this, just mock right now
        val userModel = UserModel(
            User(),
            // app.currentUser
        )
        val userViewModel = UserViewModel(userModel)
        val userController = UserController(userModel)

        setContent {
            MyApplicationTheme {
                Router(loginViewModel, userViewModel, userController)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        landmarkRepository.close()
        badgeRepository.close()
        journalEntryRepository.close()
        userRepository.close()
    }
}