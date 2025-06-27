package com.example.emtyapp.data.Entities

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val nom: String,
    val prenom: String,
    val email: String,
    val password: String,
    val phone: String,
    val address: String
)