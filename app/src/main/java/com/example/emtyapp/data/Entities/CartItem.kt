package com.example.emtyapp.data.Entities
import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val id: String = "",
    val userId: String = "",
    val product: Product,
    val quantity: Int,
    val dateAdded: String = System.currentTimeMillis().toString()
)