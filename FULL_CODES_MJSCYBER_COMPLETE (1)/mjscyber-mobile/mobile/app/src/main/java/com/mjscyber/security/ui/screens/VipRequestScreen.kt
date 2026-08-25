package com.mjscyber.security.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mjscyber.security.data.api.ApiClient
import com.mjscyber.security.data.models.SiteRequest
import com.mjscyber.security.data.models.VipRequest
import kotlinx.coroutines.launch

/**
 * VipRequestScreen – Create VIP Protection request – matches website StudentDashboard
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipRequestScreen(onBack: () -> Unit, onSuccess: () -> Unit) {
    val context = LocalContext.current
    val api = remember { ApiClient.getApi(context) }
    val scope = rememberCoroutineScope()

    var clientName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var guards by remember { mutableStateOf("2") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = { TopAppBar(title = { Text("VIP Protection Request") }, navigationIcon = { IconButton(onClick = onBack) { Text("←") } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = clientName, onValueChange = { clientName = it }, label = { Text("Client Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Start Date YYYY-MM-DD") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("End Date YYYY-MM-DD") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = guards, onValueChange = { guards = it }, label = { Text("Guards Needed") }, modifier = Modifier.fillMaxWidth())

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(onClick = {
                loading = true
                scope.launch {
                    try {
                        api.createVipRequest(VipRequest(clientName, location, startDate, endDate, guards.toIntOrNull() ?: 1))
                        Log.d("VipRequest", "Created VIP request")
                        onSuccess()
                    } catch (e: Exception) {
                        Log.e("VipRequest", "Failed: ${e.message}")
                        error = e.message
                    } finally { loading = false }
                }
            }, modifier = Modifier.fillMaxWidth(), enabled = !loading) {
                if (loading) CircularProgressIndicator(Modifier.size(20.dp)) else Text("Submit VIP Request")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteRequestScreen(onBack: () -> Unit, onSuccess: () -> Unit) {
    val context = LocalContext.current
    val api = remember { ApiClient.getApi(context) }
    val scope = rememberCoroutineScope()

    var siteName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var guards by remember { mutableStateOf("4") }
    var shift by remember { mutableStateOf("day") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = { TopAppBar(title = { Text("Site Security Request") }, navigationIcon = { IconButton(onClick = onBack) { Text("←") } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = siteName, onValueChange = { siteName = it }, label = { Text("Site Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Start Date YYYY-MM-DD") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("End Date YYYY-MM-DD") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = guards, onValueChange = { guards = it }, label = { Text("Guards Needed") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = shift, onValueChange = { shift = it }, label = { Text("Shift: day/night/24-7") }, modifier = Modifier.fillMaxWidth())

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(onClick = {
                loading = true
                scope.launch {
                    try {
                        api.createSiteRequest(SiteRequest(siteName, location, startDate, endDate, guards.toIntOrNull() ?: 4, shift))
                        Log.d("SiteRequest", "Created Site request")
                        onSuccess()
                    } catch (e: Exception) {
                        Log.e("SiteRequest", "Failed: ${e.message}")
                        error = e.message
                    } finally { loading = false }
                }
            }, modifier = Modifier.fillMaxWidth(), enabled = !loading) {
                if (loading) CircularProgressIndicator(Modifier.size(20.dp)) else Text("Submit Site Request")
            }
        }
    }
}
