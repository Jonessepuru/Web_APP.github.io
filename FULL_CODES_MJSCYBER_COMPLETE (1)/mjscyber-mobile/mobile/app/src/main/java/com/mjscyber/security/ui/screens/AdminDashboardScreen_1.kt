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
import com.mjscyber.security.data.models.AdminStats
import kotlinx.coroutines.launch

/**
 * AdminDashboardScreen – Admin view if role=admin – matches website AdminDashboard.jsx 7 tabs
 * Shows Overview KPIs: Students, Courses, Pending/Active Enrolments, Certificates, VIP Pending, Site Pending, Revenue ZAR
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val api = remember { ApiClient.getApi(context) }
    val scope = rememberCoroutineScope()

    var stats by remember { mutableStateOf<AdminStats?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            stats = api.getAdminStats()
            Log.d("AdminDashboard", "Stats loaded: $stats")
        } catch (e: Exception) {
            Log.e("AdminDashboard", "Failed to load stats: ${e.message}")
            error = e.message
        } finally {
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Admin – MJSCYBER Control Centre") }, navigationIcon = {
                IconButton(onClick = onBack) { Text("←") }
            }, actions = {
                TextButton(onClick = {
                    scope.launch {
                        ApiClient.clearToken(context)
                        onLogout()
                    }
                }) { Text("Logout") }
            })
        }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    if (error != null) {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                            Text("Error: $error – ensure you are admin@mjscyber.co.za", Modifier.padding(16.dp))
                        }
                    }
                }
                item {
                    Text("Overview KPIs – Matches Website Admin", style = MaterialTheme.typography.titleSmall)
                }
                stats?.let { s ->
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatCard("Students", "${s.students}")
                            StatCard("Active Courses", "${s.courses}")
                            StatCard("Pending Enrolments", "${s.enrolmentsPending}")
                            StatCard("Active Enrolments", "${s.enrolmentsActive}")
                            StatCard("Certificates Issued", "${s.certificates}")
                            StatCard("VIP Pending", "${s.vipPending}")
                            StatCard("Site Pending", "${s.sitePending}")
                            StatCard("Revenue (ZAR)", "R ${s.revenueZar}")
                        }
                    }
                }
                item {
                    Text("Admin Actions (to be expanded)", style = MaterialTheme.typography.titleSmall)
                    Text("• Approve Enrolments: GET /enrolments → PATCH /enrolments/{id}/approve\n• Grade: PATCH /enrolments/{id}/grade\n• Issue Certificate: POST /certificates/issue/{id}\n• VIP/Site: PATCH /vip-requests/{id}, /site-security/{id}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}
