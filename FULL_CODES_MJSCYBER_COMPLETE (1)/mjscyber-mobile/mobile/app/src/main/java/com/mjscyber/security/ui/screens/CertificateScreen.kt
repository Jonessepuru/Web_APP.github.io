package com.mjscyber.security.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mjscyber.security.data.api.ApiClient
import com.mjscyber.security.data.models.Certificate
import kotlinx.coroutines.launch

/**
 * CertificateScreen – shows my certificates with serial MJS-YYYY-XXXXXXXX + PDF download
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificateScreen(
    onBack: () -> Unit,
    onVerifyClick: () -> Unit
) {
    val context = LocalContext.current
    val api = remember { ApiClient.getApi(context) }
    val scope = rememberCoroutineScope()

    var certs by remember { mutableStateOf<List<Certificate>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            certs = api.getMyCertificates()
            Log.d("CertificateScreen", "Loaded ${certs.size} certificates")
        } catch (e: Exception) {
            Log.e("CertificateScreen", "Failed: ${e.message}")
        } finally {
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Certificates") }, navigationIcon = {
                IconButton(onClick = onBack) { Text("←") }
            }, actions = {
                TextButton(onClick = onVerifyClick) { Text("Verify") }
            })
        }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (certs.isEmpty()) {
                    item { Text("No certificates yet – complete a course with ≥50%") }
                } else {
                    items(certs) { cert ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text(cert.serial, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                Text(cert.courseTitle, style = MaterialTheme.typography.titleSmall)
                                Text("Mark: ${cert.overallMark}% · Issued: ${cert.issuedAt}", style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(8.dp))
                                OutlinedButton(onClick = { Log.d("CertificateScreen", "Download PDF for ${cert.serial}") }) {
                                    Text("Download PDF")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
