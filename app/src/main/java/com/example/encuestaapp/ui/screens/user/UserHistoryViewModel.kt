package com.example.encuestaapp.ui.screens.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encuestaapp.data.model.Survey
import com.example.encuestaapp.data.model.SurveyResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class UserHistoryUiState {
    object Loading : UserHistoryUiState()
    data class Success(val participations: List<Pair<SurveyResponse, Survey?>>) : UserHistoryUiState()
    data class Error(val message: String) : UserHistoryUiState()
    object Empty : UserHistoryUiState()
}

class UserHistoryViewModel : ViewModel() {
    var uiState by mutableStateOf<UserHistoryUiState>(UserHistoryUiState.Loading)
        private set

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    init {
        loadHistory()
    }

    fun loadHistory() {
        val userId = auth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            uiState = UserHistoryUiState.Loading
            try {
                // Usamos collectionGroup para buscar en todas las subcolecciones "responses"
                // Nota: Esto requiere configurar un índice en la consola de Firebase si se usa orderBy o filtros complejos
                val snapshot = firestore.collectionGroup("responses")
                    .whereEqualTo("userId", userId)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                if (snapshot.isEmpty) {
                    uiState = UserHistoryUiState.Empty
                } else {
                    val responses = snapshot.toObjects(SurveyResponse::class.java)
                    
                    // Para cada respuesta, intentamos obtener los datos de la encuesta
                    val participations = responses.map { response ->
                        val surveyDoc = firestore.collection("surveys").document(response.surveyId).get().await()
                        val survey = if (surveyDoc.exists()) surveyDoc.toObject(Survey::class.java) else null
                        Pair(response, survey)
                    }
                    
                    uiState = UserHistoryUiState.Success(participations)
                }
            } catch (e: Exception) {
                uiState = UserHistoryUiState.Error(e.localizedMessage ?: "Error al cargar el historial")
            }
        }
    }
}
