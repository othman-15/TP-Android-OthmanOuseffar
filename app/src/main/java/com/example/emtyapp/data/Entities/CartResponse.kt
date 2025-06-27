package com.example.emtyapp.data.Entities
import kotlinx.serialization.Serializable

@Serializable
data class CartResponse(
    val items: List<CartItem>,
    val total: Double,
    val itemsCount: Int
)