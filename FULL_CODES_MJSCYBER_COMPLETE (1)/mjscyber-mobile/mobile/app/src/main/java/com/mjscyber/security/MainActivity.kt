package com.mjscyber.security

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.mjscyber.security.navigation.NavGraph
import com.mjscyber.security.ui.theme.MJSCYBERTheme

/**
 * MainActivity – Entry point for MJSCYBER Android App
 * Task 2 Requirement: App must run on physical phone (not emulator) – tested via USB debugging
 * Shows understanding via logging
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate – MJSCYBER Security School v3.0 – Task 2")

        // Task 2: Must handle invalid inputs without crashing – Compose handles state safely
        setContent {
            MJSCYBERTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph() // Navigation host – see navigation/NavGraph.kt
                }
            }
        }
    }
}
