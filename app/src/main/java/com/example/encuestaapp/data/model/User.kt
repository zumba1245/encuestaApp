package com.example.encuestaapp.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val role: String = "user" // roles: user, admin
)
