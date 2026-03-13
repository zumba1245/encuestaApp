package com.example.encuestaapp.ui.screens.register

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encuestaapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class RegisterViewModel : ViewModel() {
    var uiState by mutableStateOf(RegisterUiState())
        private set

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email, emailError = null)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, passwordError = null)
    }

    fun onConfirmPasswordChange(password: String) {
        uiState = uiState.copy(confirmPassword = password, confirmPasswordError = null)
    }

    private fun validateFields(): Boolean {
        var isValid = true
        if (!Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()) {
            uiState = uiState.copy(emailError = "Email inválido")
            isValid = false
        }
        if (uiState.password.length < 6) {
            uiState = uiState.copy(passwordError = "Mínimo 6 caracteres")
            isValid = false
        }
        if (uiState.password != uiState.confirmPassword) {
            uiState = uiState.copy(confirmPasswordError = "Las contraseñas no coinciden")
            isValid = false
        }
        return isValid
    }

    fun register() {
        if (!validateFields()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                // 1. Crear en Auth
                val result = auth.createUserWithEmailAndPassword(uiState.email, uiState.password).await()
                val uid = result.user?.uid ?: throw Exception("Error al obtener UID")

                // 2. Crear documento en Firestore con rol "user"
                val newUser = User(
                    uid = uid,
                    email = uiState.email,
                    role = "user"
                )
                firestore.collection("users").document(uid).set(newUser).await()

                uiState = uiState.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.localizedMessage)
            }
        }
    }
}
