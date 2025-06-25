package com.example.emtyapp.ui.cart

import com.example.emtyapp.data.Entities.Product

sealed class CartIntent {
    object LoadCart : CartIntent()
    data class AddToCart(val product: Product) : CartIntent()
    data class UpdateQuantity(val cartItemId: String, val quantity: Int) : CartIntent()
    data class RemoveItem(val cartItemId: String) : CartIntent()
    object ClearCart : CartIntent()
    object Checkout : CartIntent()
}