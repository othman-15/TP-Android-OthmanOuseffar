
package com.example.emtyapp.data.Repository

import com.example.emtyapp.data.Entities.CartItem
import com.example.emtyapp.data.Entities.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor() : CartRepository {


    private val cartItems = mutableMapOf<String, CartItem>()
    private val _cartItemsFlow = MutableStateFlow<Map<String, CartItem>>(emptyMap())

    override suspend fun addToCart(product: Product) {
        addToCart(product, 1)
    }

    override suspend fun addToCart(product: Product, quantity: Int) {
        val productId = product.id

        if (cartItems.containsKey(productId)) {

            val existingItem = cartItems[productId]!!
            cartItems[productId] = existingItem.copy(quantity = existingItem.quantity + quantity)
        } else {

            cartItems[productId] = CartItem(
                product = product,
                quantity = quantity
            )
        }


        _cartItemsFlow.value = cartItems.toMap()
    }

    override fun getCartItems(): Flow<List<CartItem>> {

        return _cartItemsFlow.asStateFlow().map { it.values.toList() }
    }

    override suspend fun updateQuantity(cartItemId: String, quantity: Int) {

        cartItems[cartItemId]?.let { item ->
            if (quantity <= 0) {
                cartItems.remove(cartItemId)
            } else {
                cartItems[cartItemId] = item.copy(quantity = quantity)
            }


            _cartItemsFlow.value = cartItems.toMap()
        }
    }

    override suspend fun removeFromCart(cartItemId: String) {

        cartItems.remove(cartItemId)


        _cartItemsFlow.value = cartItems.toMap()
    }

    override suspend fun clearCart() {
        cartItems.clear()


        _cartItemsFlow.value = cartItems.toMap()
    }

    override suspend fun getCartTotal(): Double {
        return cartItems.values.sumOf { it.product.price * it.quantity }
    }

    override suspend fun getCartItemsCount(): Int {
        return cartItems.values.sumOf { it.quantity }
    }
}