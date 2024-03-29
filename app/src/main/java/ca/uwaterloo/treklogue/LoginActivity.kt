package ca.uwaterloo.treklogue

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import ca.uwaterloo.treklogue.ui.screens.LoginScreen
import ca.uwaterloo.treklogue.ui.theme.MyApplicationTheme
import ca.uwaterloo.treklogue.ui.viewModels.EventSeverity
import ca.uwaterloo.treklogue.ui.viewModels.LoginEvent
import ca.uwaterloo.treklogue.ui.viewModels.LoginViewModel
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fast-track task list screen if we are logged in
        if (app.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        lifecycleScope.launch {
            // Subscribe to navigation and message-logging events
            loginViewModel.event
                .collect { event ->
                    when (event) {
                        is LoginEvent.GoToMap -> {
                            event.process()

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        is LoginEvent.ShowMessage -> event.process()
                    }
                }
        }

        setContent {
            MyApplicationTheme {
                LoginScreen(loginViewModel)
            }
        }
    }

    private fun LoginEvent.process() {
        when (severity) {
            EventSeverity.INFO -> Log.i(TAG(), message)
            EventSeverity.ERROR -> {
                Log.e(TAG(), message)
                Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
