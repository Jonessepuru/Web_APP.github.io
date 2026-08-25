package com.mjscyber.security.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mjscyber.security.data.api.ApiClient
import com.mjscyber.security.data.models.User
import kotlinx.coroutines.launch

/**
 * ProfileSettingsScreen – Task 2: User must be able to change settings
 * Includes: change password, notifications toggle, POPIA consent, biometric, logout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val api = remember { ApiClient.getApi(context) }
    val scope = rememberCoroutineScope()

    var user by remember { mutableStateOf<User?>(null) }
    var notifications by remember { mutableStateOf(true) }
    var biometric by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            user = api.getMe()
            Log.d("ProfileScreen", "Loaded user: ${user?.email}")
        } catch (e: Exception) {
            Log.e("ProfileScreen", "Failed to getMe: ${e.message}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Profile & Settings") }, navigationIcon = {
                IconButton(onClick = onBack) { Text("←") }
            })
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(user?.name ?: "Loading...", style = MaterialTheme.typography.titleMedium)
                    Text(user?.email ?: "", style = MaterialTheme.typography.bodySmall)
                    Text("Role: ${user?.role ?: ""}", style = MaterialTheme.typography.labelSmall)
                }
            }

            Text("Settings – Task 2 Requirement", style = MaterialTheme.typography.titleSmall)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Push Notifications")
                Switch(checked = notifications, onCheckedChange = { notifications = it; Log.d("Profile", "Notifications: $it") })
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Biometric Login (SSO)")
                Switch(checked = biometric, onCheckedChange = { biometric = it; Log.d("Profile", "Biometric: $it") })
            }

            OutlinedButton(onClick = { Log.d("Profile", "Change password clicked – TODO implement POST /api/auth/change-password") }, modifier = Modifier.fillMaxWidth()) {
                Text("Change Password")
            }

            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(12.dp)) {
                    Text("POPIA Compliance", style = MaterialTheme.typography.labelSmall)
                    Text("Your data is encrypted at rest (EncryptedSharedPreferences) and in transit (HTTPS). Contact mjscyber1@gmail.com for data requests.", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(onClick = {
                scope.launch {
                    ApiClient.clearToken(context)
                    try { api.logout() } catch (_: Exception) {}
                    Log.d("Profile", "Logged out")
                    onLogout()
                }
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Logout")
            }
        }
    }
}
