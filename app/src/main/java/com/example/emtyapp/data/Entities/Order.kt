package com.example.emtyapp.data.Entities
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String = "",
    val userId: String,
    val products: List<OrderProduct>,
    val shippingAddress: String,
    val total: Double,
    val date: String,
    val status: String = "pending"
)