package com.example.encuestaapp.data.model

data class Survey(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val createdAt: Long = 0L,
    val question: String = "",
    val options: List<String> = emptyList(),
    val responsesCount: Int = 0,
    val isActive: Boolean = true,
    val isPublic: Boolean = true,
    val imageUrl: String? = null // Added imageUrl
)
