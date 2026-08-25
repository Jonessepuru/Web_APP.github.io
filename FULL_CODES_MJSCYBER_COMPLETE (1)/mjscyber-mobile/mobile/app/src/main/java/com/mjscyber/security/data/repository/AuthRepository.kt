package com.mjscyber.security.data.repository

import android.content.Context
import android.util.Log
import com.mjscyber.security.data.api.ApiClient
import com.mjscyber.security.data.models.LoginRequest
import com.mjscyber.security.data.models.RegisterRequest
import com.mjscyber.security.data.models.User

/**
 * AuthRepository – handles login/register, encrypted storage, SSO
 * Task 2: Encrypt password, SSO, logging for understanding
 */
class AuthRepository(private val context: Context) {

    private val api = ApiClient.getApi(context)

    // Login – returns User, saves token encrypted
    suspend fun login(email: String, password: String): Result<User> {
        Log.d("AuthRepo", "Login attempt for $email")
        return try {
            // Input validation – Task 2: handle invalid inputs without crashing
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return Result.failure(Exception("Invalid email format"))
            }
            if (password.length < 6) {
                return Result.failure(Exception("Password must be at least 6 characters"))
            }

            val response = api.login(LoginRequest(email, password))
            // Save token encrypted – Task 2 encrypt requirement
            response.token?.let { ApiClient.saveToken(context, it) }
            ApiClient.saveUserRole(context, response.role)

            Log.d("AuthRepo", "Login success – role: ${response.role}")
            Result.success(User(response.id, response.name, response.email, response.role))
        } catch (e: Exception) {
            Log.e("AuthRepo", "Login failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String, saId: String?): Result<User> {
        Log.d("AuthRepo", "Register attempt for $email")
        return try {
            if (name.length < 2) return Result.failure(Exception("Name too short"))
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return Result.failure(Exception("Invalid email format"))
            }
            if (password.length < 8) return Result.failure(Exception("Password must be at least 8 characters"))

            val response = api.register(RegisterRequest(name, email, password, saId))
            response.token?.let { ApiClient.saveToken(context, it) }
            ApiClient.saveUserRole(context, response.role)

            Log.d("AuthRepo", "Register success")
            Result.success(User(response.id, response.name, response.email, response.role))
        } catch (e: Exception) {
            Log.e("AuthRepo", "Register failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getMe(): Result<User> {
        return try {
            val user = api.getMe()
            Log.d("AuthRepo", "getMe success: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("AuthRepo", "getMe failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun logout() {
        try {
            api.logout()
        } catch (e: Exception) {
            Log.w("AuthRepo", "Logout API failed, clearing local anyway: ${e.message}")
        } finally {
            ApiClient.clearToken(context)
        }
    }

    fun isLoggedIn(): Boolean {
        return ApiClient.getToken(context) != null
    }

    fun getRole(): String? = ApiClient.getUserRole(context)
}
