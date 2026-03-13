package com.example.encuestaapp.ui.screens.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encuestaapp.data.model.Survey
import com.example.encuestaapp.data.model.SurveyResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class VotingViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    var selectedSurvey by mutableStateOf<Survey?>(null)
    var isVoting by mutableStateOf(false)
    var voteSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onSurveySelected(survey: Survey) {
        selectedSurvey = survey
        voteSuccess = false
        errorMessage = null
    }

    fun dismissDialog() {
        selectedSurvey = null
    }

    fun submitVote(option: String) {
        val userId = auth.currentUser?.uid ?: return
        val survey = selectedSurvey ?: return

        viewModelScope.launch {
            isVoting = true
            try {
                // 1. Verificar si ya votó (opcional, pero buena práctica)
                val existingVote = firestore.collection("surveys")
                    .document(survey.id)
                    .collection("responses")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                if (!existingVote.isEmpty) {
                    errorMessage = "Ya has participado en esta encuesta"
                    isVoting = false
                    return@launch
                }

                // 2. Registrar el voto
                val response = SurveyResponse(
                    userId = userId,
                    surveyId = survey.id,
                    optionSelected = option,
                    timestamp = System.currentTimeMillis()
                )

                firestore.collection("surveys")
                    .document(survey.id)
                    .collection("responses")
                    .add(response)
                    .await()

                // 3. Incrementar el contador global
                firestore.collection("surveys")
                    .document(survey.id)
                    .update("responsesCount", FieldValue.increment(1))
                    .await()

                voteSuccess = true
                selectedSurvey = null
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Error al registrar el voto"
            } finally {
                isVoting = false
            }
        }
    }
}
