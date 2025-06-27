package com.example.emtyapp.data.Repository

import com.example.emtyapp.data.Entities.CartItem
import com.example.emtyapp.data.Entities.Order
import com.example.emtyapp.data.Entities.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>
    suspend fun addToCart(product: Product, quantity: Int = 1)
    suspend fun removeFromCart(cartItemId: String)
    suspend fun updateQuantity(cartItemId: String, quantity: Int)
    suspend fun clearCart()
    suspend fun getCartTotal(): Double
    suspend fun getCartItemsCount(): Int
    suspend fun checkout(shippingAddress: String): Order
}
