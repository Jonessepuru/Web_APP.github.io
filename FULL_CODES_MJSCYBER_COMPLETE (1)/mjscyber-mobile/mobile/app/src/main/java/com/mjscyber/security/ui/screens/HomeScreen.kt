package com.mjscyber.security.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mjscyber.security.data.api.ApiClient
import com.mjscyber.security.data.models.Certificate
import com.mjscyber.security.data.models.Enrolment
import kotlinx.coroutines.launch

/**
 * HomeScreen – Dashboard after login – matches website StudentDashboard
 * Shows My Courses, Certificates, Quick actions VIP/Site/Verify
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCourses: () -> Unit,
    onNavigateToCertificates: () -> Unit,
    onNavigateToVerify: () -> Unit,
    onNavigateToVip: () -> Unit,
    onNavigateToSite: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val api = remember { ApiClient.getApi(context) }
    val scope = rememberCoroutineScope()

    var enrolments by remember { mutableStateOf<List<Enrolment>>(emptyList()) }
    var certs by remember { mutableStateOf<List<Certificate>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        Log.d("HomeScreen", "Loading enrolments + certificates")
        scope.launch {
            try {
                enrolments = api.getMyEnrolments()
                certs = api.getMyCertificates()
                Log.d("HomeScreen", "Loaded ${enrolments.size} enrolments, ${certs.size} certs")
            } catch (e: Exception) {
                Log.e("HomeScreen", "Failed to load: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home – MJSCYBER") },
                actions = {
                    IconButton(onClick = onNavigateToProfile) { Text("⚙️") }
                    IconButton(onClick = {
                        scope.launch {
                            ApiClient.getApi(context).logout()
                            ApiClient.clearToken(context)
                            onLogout()
                        }
                    }) { Text("Logout") }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Hi, Student – Welcome back!", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Text("${enrolments.size} Active Enrolments · ${certs.size} Certificates", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                item {
                    Text("Quick Actions", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = onNavigateToCourses, modifier = Modifier.weight(1f)) { Text("My Courses") }
                        Button(onClick = onNavigateToCertificates, modifier = Modifier.weight(1f)) { Text("Certificates") }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onNavigateToVerify, modifier = Modifier.weight(1f)) { Text("Verify QR") }
                        OutlinedButton(onClick = onNavigateToVip, modifier = Modifier.weight(1f)) { Text("VIP Request") }
                    }
                    OutlinedButton(onClick = onNavigateToSite, modifier = Modifier.fillMaxWidth()) { Text("Site Security Request") }
                }
                item {
                    Text("My Enrolments", style = MaterialTheme.typography.titleSmall)
                    if (enrolments.isEmpty()) Text("No enrolments yet – enrol in a course", style = MaterialTheme.typography.bodySmall)
                    else enrolments.forEach { e ->
                        Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Text(e.course?.title ?: e.courseId, style = MaterialTheme.typography.bodyMedium)
                                Text("Status: ${e.status} · Mark: ${e.overallMark ?: "—"}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
