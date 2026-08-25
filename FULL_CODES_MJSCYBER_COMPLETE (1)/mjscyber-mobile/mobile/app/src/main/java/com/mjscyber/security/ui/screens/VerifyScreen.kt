package com.mjscyber.security.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mjscyber.security.data.api.ApiClient
import kotlinx.coroutines.launch

/**
 * VerifyScreen – Innovative feature: QR scanner + manual serial entry
 * Task 2: ML Kit Barcode Scanning, public verification without auth
 * Must handle invalid inputs without crashing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val api = remember { ApiClient.getApi(context) }
    val scope = rememberCoroutineScope()

    var serial by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }
    var isValid by remember { mutableStateOf<Boolean?>(null) }
    var loading by remember { mutableStateOf(false) }

    // Regex from cleaned_Verify.jsx – MJS-YYYY-XXXXXXXX
    val serialRegex = remember { Regex("^MJS-\\d{4}-[A-Z0-9]{8}$") }

    fun verify() {
        if (serial.isBlank()) {
            result = "Please enter a serial number"
            isValid = false
            return
        }
        if (!serialRegex.matches(serial.uppercase())) {
            result = "Invalid format. Expected MJS-YYYY-XXXXXXXX (e.g., MJS-2026-A1B2C3D4)"
            isValid = false
            Log.w("VerifyScreen", "Invalid serial format: $serial")
            return
        }

        loading = true
        scope.launch {
            try {
                val res = api.verifyCertificate(serial.uppercase())
                Log.d("VerifyScreen", "Verify response: valid=${res.valid}")
                isValid = res.valid
                result = if (res.valid) {
                    "✅ VALID\nSerial: ${res.serial}\nStudent: ${res.studentName}\nCourse: ${res.course}\nMark: ${res.mark}%\nIssued: ${res.issuedAt}"
                } else {
                    "❌ INVALID – ${res.message ?: "Certificate not found"}"
                }
            } catch (e: Exception) {
                Log.e("VerifyScreen", "Verify failed: ${e.message}")
                isValid = false
                result = "Error: ${e.message}"
            } finally {
                loading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Certificate Verification") }, navigationIcon = {
                IconButton(onClick = onBack) { Text("←") }
            })
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Scan certificate QR code or enter serial manually", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))

            // QR Scanner placeholder – ML Kit CameraX to be implemented
            Card(Modifier.fillMaxWidth().height(250.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📷 QR Scanner Viewfinder", style = MaterialTheme.typography.titleSmall)
                        Text("ML Kit Barcode Scanning – dashed border", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { Log.d("VerifyScreen", "Camera scan clicked – TODO implement CameraX + ML Kit") }) {
                            Text("Start Camera Scan")
                        }
                        // For WIL – you can implement CameraX preview here:
                        // https://developers.google.com/ml-kit/vision/barcode-scanning/android
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = serial,
                onValueChange = { serial = it.uppercase() },
                label = { Text("Serial e.g., MJS-2026-A1B2C3D4") },
                modifier = Modifier.fillMaxWidth(),
                isError = isValid == false
            )

            Spacer(Modifier.height(16.dp))

            Button(onClick = { verify() }, modifier = Modifier.fillMaxWidth(), enabled = !loading) {
                if (loading) CircularProgressIndicator(Modifier.size(20.dp))
                else Text("VERIFY CERTIFICATE")
            }

            Spacer(Modifier.height(24.dp))

            result?.let {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                    containerColor = if (isValid == true) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                )) {
                    Text(it, Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
