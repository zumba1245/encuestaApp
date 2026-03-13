package com.example.encuestaapp.data.model

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val location: String = "",
    val imageUrl: String = "",
    val ticketPrice: Double = 0.0,
    val totalTickets: Int = 0,
    val soldTickets: Int = 0
)
