package com.example.encuestaapp.ui.screens.admin

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.encuestaapp.data.model.Survey
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class CreateSurveyViewModel : ViewModel() {
    var title by mutableStateOf("")
    var question by mutableStateOf("")
    val options = mutableStateListOf("Opción 1", "Opción 2")
    
    var imageUri by mutableStateOf<Uri?>(null)
    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun addOption() {
        options.add("Nueva opción")
    }

    fun removeOption(index: Int) {
        if (options.size > 2) {
            options.removeAt(index)
        }
    }

    fun updateOption(index: Int, newValue: String) {
        options[index] = newValue
    }

    private suspend fun uploadImage(uri: Uri): String {
        try {
            val fileName = "survey_${UUID.randomUUID()}.jpg"
            val storage = FirebaseStorage.getInstance()
            // Referencia al archivo
            val ref = storage.reference.child("survey_images/$fileName")
            
            // Subir el archivo
            ref.putFile(uri).await()
            
            // Obtener la URL una vez subido
            return ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw Exception("Fallo al subir imagen: ${e.localizedMessage}")
        }
    }

    fun createSurvey() {
        if (title.isBlank() || question.isBlank()) {
            errorMessage = "Completa el título y la pregunta"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                var finalImageUrl: String? = null
                
                // Solo intentamos subir si el usuario seleccionó una imagen
                imageUri?.let { uri ->
                    finalImageUrl = uploadImage(uri)
                }

                val firestore = FirebaseFirestore.getInstance()
                val newDoc = firestore.collection("surveys").document()
                
                val survey = Survey(
                    id = newDoc.id,
                    title = title,
                    question = question,
                    options = options.toList(),
                    createdAt = System.currentTimeMillis(),
                    imageUrl = finalImageUrl,
                    responsesCount = 0
                )
                
                newDoc.set(survey).await()
                isSuccess = true
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Error desconocido al publicar"
            } finally {
                isLoading = false
            }
        }
    }
}
