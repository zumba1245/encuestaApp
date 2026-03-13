package com.example.encuestaapp.data.model

data class SurveyResponse(
    val userId: String = "",
    val surveyId: String = "",
    val optionSelected: String = "",
    val timestamp: Long = 0L
)
