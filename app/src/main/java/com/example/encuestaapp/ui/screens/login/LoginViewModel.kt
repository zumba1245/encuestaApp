package com.example.encuestaapp.ui.screens.login

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val isLoginSuccess: Boolean = false,
    val userRole: String? = null
)

class LoginViewModel : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email, emailError = null, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, passwordError = null, errorMessage = null)
    }

    private fun validateFields(): Boolean {
        var isValid = true
        val email = uiState.email.trim()
        val password = uiState.password

        if (email.isBlank()) {
            uiState = uiState.copy(emailError = "El correo no puede estar vacío")
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            uiState = uiState.copy(emailError = "Formato de correo inválido")
            isValid = false
        }

        if (password.isBlank()) {
            uiState = uiState.copy(passwordError = "La contraseña no puede estar vacía")
            isValid = false
        }

        return isValid
    }

    fun loginWithEmail() {
        if (!validateFields()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val result = auth.signInWithEmailAndPassword(uiState.email.trim(), uiState.password).await()
                val uid = result.user?.uid
                if (uid != null) {
                    fetchUserRole(uid)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error al iniciar sesión"
                )
            }
        }
    }

    // FUNCIÓN FALTANTE: Recuperar contraseña
    fun sendPasswordReset() {
        val email = uiState.email.trim()
        if (email.isBlank()) {
            uiState = uiState.copy(emailError = "Ingresa tu correo para recuperar la contraseña")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, infoMessage = null)
            try {
                auth.sendPasswordResetEmail(email).await()
                uiState = uiState.copy(isLoading = false, infoMessage = "Correo de recuperación enviado")
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error al enviar correo de recuperación"
                )
            }
        }
    }

    private suspend fun fetchUserRole(uid: String) {
        try {
            val document = firestore.collection("users").document(uid).get().await()
            val role = document.getString("role") ?: "user"
            uiState = uiState.copy(isLoading = false, userRole = role, isLoginSuccess = true)
        } catch (e: Exception) {
            uiState = uiState.copy(
                isLoading = false, 
                errorMessage = "Error al obtener permisos del usuario"
            )
        }
    }

    fun handleGoogleSignInResult(credential: Credential) {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                firebaseAuthWithGoogle(idToken)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = "Error al procesar la credencial de Google")
            }
        } else {
            uiState = uiState.copy(errorMessage = "Credencial de Google no válida")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val uid = result.user?.uid
                if (uid != null) {
                    fetchUserRole(uid)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error con Google Sign-In"
                )
            }
        }
    }

    fun onGoogleSignInError(message: String) {
        uiState = uiState.copy(errorMessage = message)
    }
    
    fun clearInfoMessage() {
        uiState = uiState.copy(infoMessage = null)
    }
}
