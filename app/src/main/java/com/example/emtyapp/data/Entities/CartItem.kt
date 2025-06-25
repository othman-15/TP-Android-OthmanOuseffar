package com.example.emtyapp.data.Entities

data class CartItem(

    val product: Product,
    val quantity: Int,
    val totalPrice: Double = product.price * quantity
)