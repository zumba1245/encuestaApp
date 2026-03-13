package com.example.encuestaapp.ui.screens.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encuestaapp.data.model.Survey
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch

sealed class AdminHomeUiState {
    object Loading : AdminHomeUiState()
    data class Success(val surveys: List<Survey>) : AdminHomeUiState()
    data class Error(val message: String) : AdminHomeUiState()
    object Empty : AdminHomeUiState()
}

class AdminHomeViewModel : ViewModel() {
    var uiState by mutableStateOf<AdminHomeUiState>(AdminHomeUiState.Loading)
        private set

    private val firestore = FirebaseFirestore.getInstance()

    init {
        observeSurveys()
    }

    // Escucha cambios en tiempo real
    private fun observeSurveys() {
        firestore.collection("surveys")
            .orderBy("createdAt", Query.Direction.DESCENDING) // Las más nuevas primero
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    uiState = AdminHomeUiState.Error(error.localizedMessage ?: "Error de conexión")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val surveys = snapshot.toObjects(Survey::class.java)
                    uiState = if (surveys.isEmpty()) {
                        AdminHomeUiState.Empty
                    } else {
                        AdminHomeUiState.Success(surveys)
                    }
                }
            }
    }
}
