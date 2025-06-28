package com.example.emtyapp.data.Repository

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton
import com.example.emtyapp.data.Entities.Order
import com.example.emtyapp.data.api.ApiService

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : OrderRepository {

    override suspend fun getUserOrders(userId: String): List<Order> {
        return try {
            Log.d("OrderRepository", "Fetching orders for user: $userId")


            try {
                val userOrdersResponse = apiService.getUserOrders(userId)
                if (userOrdersResponse.isSuccessful) {
                    val orders = userOrdersResponse.body() ?: emptyList()
                    Log.d("OrderRepository", "Found ${orders.size} orders via user endpoint")
                    return orders.sortedByDescending { order ->
                        // Sort by date (most recent first)
                        try {
                            order.date.toLongOrNull() ?: 0L
                        } catch (e: Exception) {
                            0L
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("OrderRepository", "User orders endpoint failed, falling back to all orders: ${e.message}")
            }

            // Fallback: Get all orders and filter by userId
            val allOrdersResponse = apiService.getAllOrders()
            if (allOrdersResponse.isSuccessful) {
                val allOrders = allOrdersResponse.body() ?: emptyList()
                val userOrders = allOrders.filter { order -> order.userId == userId }

                Log.d("OrderRepository", "Filtered ${userOrders.size} orders from ${allOrders.size} total orders")

                // Sort orders by date (most recent first)
                return userOrders.sortedByDescending { order ->
                    try {
                        // Handle both timestamp and ISO date formats
                        when {
                            order.date.toLongOrNull() != null -> order.date.toLong()
                            order.date.contains("T") -> {
                                // ISO format - convert to timestamp
                                try {
                                    java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault())
                                        .parse(order.date)?.time ?: 0L
                                } catch (e: Exception) {
                                    0L
                                }
                            }
                            else -> 0L
                        }
                    } catch (e: Exception) {
                        Log.w("OrderRepository", "Error parsing date: ${order.date}")
                        0L
                    }
                }
            } else {
                val errorMessage = when (allOrdersResponse.code()) {
                    401 -> "Non autorisé - Veuillez vous reconnecter"
                    403 -> "Accès refusé"
                    404 -> "Service de commandes non disponible"
                    500 -> "Erreur serveur - Veuillez réessayer plus tard"
                    else -> "Erreur réseau (${allOrdersResponse.code()})"
                }
                Log.e("OrderRepository", "API Error: ${allOrdersResponse.code()} - ${allOrdersResponse.message()}")
                throw Exception(errorMessage)
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Error fetching user orders: ${e.message}", e)
            when (e) {
                is java.net.UnknownHostException -> throw Exception("Pas de connexion Internet")
                is java.net.SocketTimeoutException -> throw Exception("Délai d'attente dépassé")
                is java.net.ConnectException -> throw Exception("Impossible de se connecter au serveur")
                else -> throw Exception(e.message ?: "Erreur lors du chargement des commandes")
            }
        }
    }

    override suspend fun cancelOrder(orderId: String): Boolean {
        return try {
            Log.d("OrderRepository", "Cancelling order: $orderId")
            val response = apiService.cancelOrder(orderId)
            if (response.isSuccessful) {
                val success = response.body()?.success ?: false
                Log.d("OrderRepository", "Cancel order result: $success")
                success
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Commande introuvable"
                    400 -> "Cette commande ne peut pas être annulée"
                    403 -> "Vous n'êtes pas autorisé à annuler cette commande"
                    else -> "Erreur lors de l'annulation: ${response.code()}"
                }
                Log.e("OrderRepository", "Cancel order error: ${response.code()} - ${response.message()}")
                throw Exception(errorMessage)
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Cancel Order Error: ${e.message}", e)
            throw Exception("Impossible d'annuler la commande: ${e.message}")
        }
    }

    override suspend fun reorderItems(orderId: String): Order {
        return try {
            Log.d("OrderRepository", "Reordering items from order: $orderId")
            val response = apiService.reorderItems(orderId)
            if (response.isSuccessful) {
                val newOrder = response.body() ?: throw Exception("Réponse vide du serveur")
                Log.d("OrderRepository", "Reorder successful, new order ID: ${newOrder.id}")
                newOrder
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Commande originale introuvable"
                    400 -> "Impossible de reproduire cette commande"
                    409 -> "Certains articles ne sont plus disponibles"
                    else -> "Erreur lors de la re-commande: ${response.code()}"
                }
                Log.e("OrderRepository", "Reorder error: ${response.code()} - ${response.message()}")
                throw Exception(errorMessage)
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Reorder Error: ${e.message}", e)
            throw Exception("Impossible de repasser la commande: ${e.message}")
        }
    }
}