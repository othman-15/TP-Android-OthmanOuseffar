package com.example.emtyapp.data.Entities
import kotlinx.serialization.Serializable

@Serializable
data class AddToCartRequest(
    val userId: String,
    val productId: String,
    val quantity: Int = 1
)