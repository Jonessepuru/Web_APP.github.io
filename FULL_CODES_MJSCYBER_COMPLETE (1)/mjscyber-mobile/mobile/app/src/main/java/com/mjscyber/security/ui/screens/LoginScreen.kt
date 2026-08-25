package com.mjscyber.security.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.mjscyber.security.viewmodel.AuthViewModel

/**
 * LoginScreen – Task 2: User must be able to register and log in, encrypt password, SSO
 * Handles invalid inputs without crashing, logging for understanding
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    val viewModel = remember { AuthViewModel(context) }
    val state = viewModel.uiState

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // For demo – test credentials from Test_Credentials.md
    LaunchedEffect(Unit) {
        Log.d("LoginScreen", "Launched – isLoggedIn=${state.isLoggedIn}")
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("MJSCYBER", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
            Text("Security School", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text("Sign in to continue", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; viewModel.clearError() },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.error != null
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; viewModel.clearError() },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = state.error != null
            )

            state.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    Log.d("LoginScreen", "Login button clicked – email=$email")
                    viewModel.login(email, password) { user ->
                        onLoginSuccess(user.role)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text("LOGIN")
            }

            Spacer(Modifier.height(16.dp))

            // SSO placeholder – Task 2 SSO requirement – Firebase Google Sign-In to be added
            OutlinedButton(
                onClick = { Log.d("LoginScreen", "SSO Google Sign-In clicked – TODO Firebase Auth") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign in with Google (SSO)")
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text("Don't have account? Create account")
            }

            Spacer(Modifier.height(24.dp))
            // Test credentials helper – from Test_Credentials.md
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Test Credentials:", style = MaterialTheme.typography.labelSmall)
                    Text("Admin: admin@mjscyber.co.za / Admin@123", style = MaterialTheme.typography.bodySmall)
                    Text("Student: thabo@student.co.za / Student@123", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
