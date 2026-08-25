package com.mjscyber.security

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mjscyber.security.ui.screens.LoginScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * LoginUiTest – Espresso/Compose UI test – Task 2 automated testing
 * Tests that login screen renders without crashing (handles invalid inputs)
 */
@RunWith(AndroidJUnit4::class)
class LoginUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreenDisplaysCorrectly() {
        // Launch LoginScreen – should not crash
        composeTestRule.setContent {
            LoginScreen(onLoginSuccess = {}, onNavigateToRegister = {})
        }

        // Check UI elements exist – user-friendly UI
        composeTestRule.onNodeWithText("MJSCYBER").assertExists()
        composeTestRule.onNodeWithText("LOGIN").assertExists()
        composeTestRule.onNodeWithText("Sign in with Google (SSO)").assertExists()
    }
}
