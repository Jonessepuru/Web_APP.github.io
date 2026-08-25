package com.mjscyber.security.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * MJSCYBER Theme – matches website index.css
 * --primary: 11 61 145 = #0B3D91 (navy)
 * --accent: 192 57 43 = #C0392B (red)
 * Dark theme for security school – professional
 */
private val Navy = Color(0xFF0B3D91)
private val NavyDark = Color(0xFF081A3A)
private val RedAccent = Color(0xFFC0392B)
private val RedLight = Color(0xFFFF6B6B)
private val Background = Color(0xFF0A0A0F)
private val Surface = Color(0xFF15151E)
private val Card = Color(0xFF1E1E2E)

private val DarkColorScheme = darkColorScheme(
    primary = Navy,
    secondary = RedAccent,
    tertiary = RedLight,
    background = Background,
    surface = Surface,
    surfaceVariant = Card,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun MJSCYBERTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography, // Define in Typography.kt if needed – using default for WIL
        content = content
    )
}

// Simple typography – can be extended
val Typography = androidx.compose.material3.Typography()
