package com.example.emtyapp.data.Repository

import com.example.emtyapp.data.Entities.CartItem
import com.example.emtyapp.data.Entities.Order
import com.example.emtyapp.data.Entities.OrderProduct
import com.example.emtyapp.data.Entities.Product
import com.example.emtyapp.data.api.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val profileRepository: ProfileRepository // Injection du ProfileRepository
) : CartRepository {

    private val cartItemsFlow = MutableStateFlow<List<CartItem>>(emptyList())

    // Méthode pour obtenir l'ID de l'utilisateur connecté
    private suspend fun getCurrentUserId(): String? {
        return profileRepository.currentUser.first()?.id
    }

    override fun getCartItems(): Flow<List<CartItem>> {
        return cartItemsFlow.asStateFlow()
    }

    suspend fun loadCartFromApi() {
        try {
            val userId = getCurrentUserId()
            if (userId == null) {
                // Aucun utilisateur connecté, vider le panier local
                cartItemsFlow.value = emptyList()
                return
            }

            val response = apiService.getCartItems(userId)
            if (response.isSuccessful) {
                val items = response.body() ?: emptyList()
                cartItemsFlow.value = items
            } else {
                throw Exception("Erreur lors du chargement: ${response.code()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun addToCart(product: Product, quantity: Int) {
        try {
            val userId = getCurrentUserId()
            if (userId == null) {
                throw Exception("Vous devez être connecté pour ajouter des articles au panier")
            }

            val currentItems = cartItemsFlow.value.toMutableList()
            val existingItemIndex = currentItems.indexOfFirst { it.product.id == product.id }

            if (existingItemIndex != -1) {
                val existingItem = currentItems[existingItemIndex]
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)

                val response = apiService.updateCartItem(existingItem.id, updatedItem)
                if (response.isSuccessful) {
                    val apiUpdatedItem = response.body()
                    if (apiUpdatedItem != null) {
                        currentItems[existingItemIndex] = apiUpdatedItem
                    }
                } else {
                    throw Exception("Erreur lors de la mise à jour: ${response.code()}")
                }
            } else {
                val newCartItem = CartItem(
                    userId = userId,
                    product = product,
                    quantity = quantity
                )

                val response = apiService.addToCart(newCartItem)
                if (response.isSuccessful) {
                    val apiCartItem = response.body()
                    if (apiCartItem != null) {
                        currentItems.add(apiCartItem)
                    }
                } else {
                    throw Exception("Erreur lors de l'ajout: ${response.code()}")
                }
            }

            cartItemsFlow.value = currentItems
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateQuantity(cartItemId: String, quantity: Int) {
        try {
            val userId = getCurrentUserId()
            if (userId == null) {
                throw Exception("Vous devez être connecté pour modifier le panier")
            }

            if (quantity <= 0) {
                removeFromCart(cartItemId)
                return
            }

            val currentItems = cartItemsFlow.value.toMutableList()
            val itemIndex = currentItems.indexOfFirst { it.id == cartItemId }

            if (itemIndex != -1) {
                val updatedItem = currentItems[itemIndex].copy(quantity = quantity)
                val response = apiService.updateCartItem(cartItemId, updatedItem)

                if (response.isSuccessful) {
                    val apiUpdatedItem = response.body()
                    if (apiUpdatedItem != null) {
                        currentItems[itemIndex] = apiUpdatedItem
                        cartItemsFlow.value = currentItems
                    }
                } else {
                    throw Exception("Erreur lors de la mise à jour: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun removeFromCart(cartItemId: String) {
        try {
            val userId = getCurrentUserId()
            if (userId == null) {
                throw Exception("Vous devez être connecté pour modifier le panier")
            }

            val response = apiService.removeCartItem(cartItemId)

            if (response.isSuccessful) {
                val currentItems = cartItemsFlow.value.toMutableList()
                currentItems.removeAll { it.id == cartItemId }
                cartItemsFlow.value = currentItems
            } else {
                throw Exception("Erreur lors de la suppression: ${response.code()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun clearCart() {
        try {
            val userId = getCurrentUserId()
            if (userId == null) {
                // Si pas d'utilisateur connecté, vider juste le panier local
                cartItemsFlow.value = emptyList()
                return
            }

            // Tentative avec l'endpoint dédié
            val response = apiService.clearCart(userId)

            if (response.isSuccessful) {
                cartItemsFlow.value = emptyList()
                return
            } else if (response.code() == 404) {
                // Endpoint non disponible, utiliser la méthode alternative
                clearCartByIndividualDeletion()
                return
            } else {
                throw Exception("Erreur lors du vidage du panier: ${response.code()}")
            }
        } catch (e: Exception) {
            // En cas d'erreur réseau ou 404, essayer la méthode alternative
            if (e.message?.contains("404") == true || e is java.net.ConnectException) {
                clearCartByIndividualDeletion()
            } else {
                throw e
            }
        }
    }

    // Méthode de fallback - suppression individuelle
    private suspend fun clearCartByIndividualDeletion() {
        val currentItems = cartItemsFlow.value.toList()

        if (currentItems.isEmpty()) {
            return
        }

        for (item in currentItems) {
            try {
                val response = apiService.removeCartItem(item.id)
                if (!response.isSuccessful) {
                    // Recharger le panier pour synchroniser l'état
                    loadCartFromApi()
                    throw Exception("Erreur lors de la suppression de l'item ${item.id}: ${response.code()}")
                }
            } catch (e: Exception) {
                loadCartFromApi()
                throw e
            }
        }

        // Si tout s'est bien passé
        cartItemsFlow.value = emptyList()
    }

    override suspend fun getCartTotal(): Double {
        return cartItemsFlow.value.sumOf { it.product.price * it.quantity }
    }

    override suspend fun getCartItemsCount(): Int {
        return cartItemsFlow.value.sumOf { it.quantity }
    }

    override suspend fun checkout(shippingAddress: String): Order {
        try {
            val userId = getCurrentUserId()
            if (userId == null) {
                throw Exception("Vous devez être connecté pour passer commande")
            }

            val cartItems = cartItemsFlow.value
            if (cartItems.isEmpty()) {
                throw Exception("Le panier est vide")
            }

            val orderProducts = cartItems.map { cartItem ->
                OrderProduct(
                    productId = cartItem.product.id,
                    quantity = cartItem.quantity,
                    price = cartItem.product.price
                )
            }

            val order = Order(
                userId = userId,
                products = orderProducts,
                shippingAddress = shippingAddress,
                total = getCartTotal(),
                date = System.currentTimeMillis().toString(),
                status = "pending"
            )

            val response = apiService.createOrder(order)

            if (response.isSuccessful) {
                val createdOrder = response.body()
                if (createdOrder != null) {
                    clearCart()
                    return createdOrder
                } else {
                    throw Exception("Réponse vide du serveur")
                }
            } else {
                throw Exception("Erreur lors de la création de la commande: ${response.code()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }


    suspend fun clearLocalCart() {
        cartItemsFlow.value = emptyList()
    }
}
