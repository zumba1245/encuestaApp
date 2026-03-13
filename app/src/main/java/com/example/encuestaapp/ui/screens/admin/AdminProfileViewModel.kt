package com.example.encuestaapp.ui.screens.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encuestaapp.data.preferences.ThemePreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AdminProfileUiState(
    val email: String = "",
    val role: String = "Cargando...",
    val dynamicColorEnabled: Boolean = false,
    val isLoading: Boolean = false
)

class AdminProfileViewModel(
    private val themePreferences: ThemePreferences
) : ViewModel() {

    var uiState by mutableStateOf(AdminProfileUiState())
        private set

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            uiState = uiState.copy(email = currentUser.email ?: "", isLoading = true)
            viewModelScope.launch {
                try {
                    val document = firestore.collection("users")
                        .document(currentUser.uid)
                        .get()
                        .await()
                    
                    val role = document.getString("role") ?: "Usuario"
                    val dynamicEnabled = themePreferences.dynamicColorEnabled.first()
                    
                    uiState = uiState.copy(
                        role = role,
                        dynamicColorEnabled = dynamicEnabled,
                        isLoading = false
                    )
                } catch (e: Exception) {
                    uiState = uiState.copy(role = "Error al cargar", isLoading = false)
                }
            }
        }
    }

    fun toggleDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            themePreferences.setDynamicColorEnabled(enabled)
            uiState = uiState.copy(dynamicColorEnabled = enabled)
        }
    }

    fun logout() {
        auth.signOut()
    }
}
