package com.example.myapplication.ui.login

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.room.UserDatabaseHelper
import com.example.myapplication.data.room.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LoginUiState(
    val email: String = "chercheur.oncologie@pierre-fabre.com",
    val password: String = "pierre-fabre-2026",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = UserDatabaseHelper.getInstance(application)

    var uiState by mutableStateOf(LoginUiState())
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (dbHelper.getUserCount() == 0) {
                dbHelper.insertUser(
                    UserEntity(
                        email = "chercheur.oncologie@pierre-fabre.com",
                        password = "pierre-fabre-2026",
                        fullName = "Chercheur Oncologue Pierre Fabre"
                    )
                )
            }
        }
    }

    fun onEmailChanged(newEmail: String) {
        uiState = uiState.copy(email = newEmail, errorMessage = null)
    }

    fun onPasswordChanged(newPassword: String) {
        uiState = uiState.copy(password = newPassword, errorMessage = null)
    }

    fun login(onSuccess: () -> Unit) {
        val email = uiState.email.trim()
        val pwd = uiState.password

        if (email.isBlank() || pwd.isBlank()) {
            uiState = uiState.copy(errorMessage = "Veuillez renseigner votre email et mot de passe.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            val user = withContext(Dispatchers.IO) { dbHelper.getUserByEmail(email) }

            if (user != null && user.password == pwd) {
                uiState = uiState.copy(isLoading = false, isLoggedIn = true)
                onSuccess()
            } else {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Email ou mot de passe incorrect."
                )
            }
        }
    }

    fun loginAsDemo(onSuccess: () -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            withContext(Dispatchers.IO) {
                if (dbHelper.getUserCount() == 0) {
                    dbHelper.insertUser(
                        UserEntity(
                            email = "chercheur.oncologie@pierre-fabre.com",
                            password = "pierre-fabre-2026",
                            fullName = "Chercheur Oncologue Pierre Fabre"
                        )
                    )
                }
            }
            uiState = uiState.copy(
                email = "chercheur.oncologie@pierre-fabre.com",
                password = "pierre-fabre-2026",
                isLoading = false,
                isLoggedIn = true
            )
            onSuccess()
        }
    }
}
