package com.example.emtyapp.ui.cart

import com.example.emtyapp.data.Entities.Product

sealed class CartIntent {
    object LoadCart : CartIntent()
    data class AddToCart(val product: Product, val quantity: Int = 1) : CartIntent()
    data class UpdateQuantity(val cartItemId: String, val quantity: Int) : CartIntent()
    data class RemoveItem(val cartItemId: String) : CartIntent()
    object ClearCart : CartIntent()
    data class Checkout(val shippingAddress: String) : CartIntent()
}