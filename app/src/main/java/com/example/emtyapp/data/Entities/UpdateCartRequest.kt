package com.example.emtyapp.data.Entities
import kotlinx.serialization.Serializable

@Serializable
data class UpdateCartRequest(
    val quantity: Int
)