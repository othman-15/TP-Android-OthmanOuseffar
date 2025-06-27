package com.example.emtyapp.data.Entities
import kotlinx.serialization.Serializable

@Serializable
data class Favorite(
    val id: String = "",
    val userId: String,
    val productId: String
)