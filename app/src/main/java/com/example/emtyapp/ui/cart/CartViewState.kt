package com.example.emtyapp.ui.cart

import com.example.emtyapp.data.Entities.CartItem



sealed class CartViewState {
    object Loading : CartViewState()
    object EmptyCart : CartViewState()
    object NotLoggedIn : CartViewState() // Nouvel état
    object CheckoutSuccess : CartViewState()
    data class CartLoaded(
        val items: List<CartItem>,
        val total: Double,
        val itemCount: Int
    ) : CartViewState()
    data class Error(val message: String) : CartViewState()
}