package com.example.emtyapp.ui.cart

import com.example.emtyapp.data.Entities.CartItem

sealed class CartViewState {
    object Loading : CartViewState()
    data class CartLoaded(
        val items: List<CartItem>,
        val totalPrice: Double,
        val itemsCount: Int
    ) : CartViewState()
    object EmptyCart : CartViewState()
    data class Error(val message: String) : CartViewState()
    object CheckoutSuccess : CartViewState()
}