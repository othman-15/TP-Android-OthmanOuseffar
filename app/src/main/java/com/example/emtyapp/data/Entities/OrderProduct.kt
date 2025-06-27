package com.example.emtyapp.data.Entities
import kotlinx.serialization.Serializable

@Serializable
data class OrderProduct(
    val productId: String,
    val quantity: Int,
    val price: Double
)