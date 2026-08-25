package com.mjscyber.security.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjscyber.security.data.models.User
import com.mjscyber.security.data.repository.AuthRepository
import kotlinx.coroutines.launch

/**
 * AuthViewModel – MVVM for login/register
 * Task 2: Logging to show understanding, handle invalid inputs without crashing
 */
class AuthViewModel(context: Context) : ViewModel() {

    private val repo = AuthRepository(context)

    var uiState by mutableStateOf(AuthUiState())
        private set

    data class AuthUiState(
        val isLoading: Boolean = false,
        val user: User? = null,
        val error: String? = null,
        val isLoggedIn: Boolean = false
    )

    init {
        Log.d("AuthVM", "Init – checking if logged in: ${repo.isLoggedIn()}")
        uiState = uiState.copy(isLoggedIn = repo.isLoggedIn())
    }

    fun login(email: String, password: String, onSuccess: (User) -> Unit) {
        Log.d("AuthVM", "login() called for $email")
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val result = repo.login(email, password)
            result.onSuccess { user ->
                Log.d("AuthVM", "login success – ${user.role}")
                uiState = uiState.copy(isLoading = false, user = user, isLoggedIn = true)
                onSuccess(user)
            }.onFailure { e ->
                Log.e("AuthVM", "login failed: ${e.message}")
                uiState = uiState.copy(isLoading = false, error = e.message ?: "Login failed")
            }
        }
    }

    fun register(name: String, email: String, password: String, saId: String?, onSuccess: (User) -> Unit) {
        Log.d("AuthVM", "register() called for $email")
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val result = repo.register(name, email, password, saId)
            result.onSuccess { user ->
                Log.d("AuthVM", "register success")
                uiState = uiState.copy(isLoading = false, user = user, isLoggedIn = true)
                onSuccess(user)
            }.onFailure { e ->
                Log.e("AuthVM", "register failed: ${e.message}")
                uiState = uiState.copy(isLoading = false, error = e.message ?: "Registration failed")
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        Log.d("AuthVM", "logout() called")
        viewModelScope.launch {
            repo.logout()
            uiState = AuthUiState()
            onLoggedOut()
        }
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }
}
