package com.example.emtyapp.data.Repository

import com.example.emtyapp.data.Entities.Order
import com.example.emtyapp.data.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getOrdersByUserId(userId: String): List<Order> {
        return try {
            val response = apiService.getOrders(userId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                throw Exception("Erreur ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Erreur réseau: ${e.message}")
        }
    }

    suspend fun createOrder(order: Order): Order? {
        return try {
            val response = apiService.createOrder(order)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Erreur ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Erreur réseau: ${e.message}")
        }
    }
}