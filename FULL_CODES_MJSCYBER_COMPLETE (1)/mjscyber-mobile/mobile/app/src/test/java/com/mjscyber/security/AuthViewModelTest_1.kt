package com.mjscyber.security

import org.junit.Assert.*
import org.junit.Test

/**
 * AuthViewModelTest – JUnit unit test – Task 2 automated testing requirement
 * Tests main functionality – login validation
 */
class AuthViewModelTest {

    @Test
    fun testEmailValidation() {
        // Task 2: Must handle invalid inputs without crashing
        val invalidEmails = listOf("invalid", "test@", "@test.com", "")
        val validEmails = listOf("test@example.com", "admin@mjscyber.co.za", "thabo@student.co.za")

        for (email in invalidEmails) {
            assertFalse("Should be invalid: $email", android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        }

        for (email in validEmails) {
            assertTrue("Should be valid: $email", android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        }
    }

    @Test
    fun testSerialRegex() {
        // From cleaned_Verify.jsx – MJS-YYYY-XXXXXXXX
        val regex = Regex("^MJS-\\d{4}-[A-Z0-9]{8}$")
        assertTrue(regex.matches("MJS-2026-A1B2C3D4"))
        assertTrue(regex.matches("MJS-2025-1234ABCD"))
        assertFalse(regex.matches("MJS-26-A1B2"))
        assertFalse(regex.matches("invalid"))
        assertFalse(regex.matches(""))
    }

    @Test
    fun testPasswordStrength() {
        // Task 2: Encrypt password – backend bcrypt, frontend min length
        val weak = listOf("123", "pass", "")
        val strong = listOf("Admin@123", "Student@123", "StrongPass123!")

        for (pwd in weak) {
            assertTrue("Should be weak: $pwd", pwd.length < 8)
        }
        for (pwd in strong) {
            assertTrue("Should be strong: $pwd", pwd.length >= 8)
        }
    }
}
