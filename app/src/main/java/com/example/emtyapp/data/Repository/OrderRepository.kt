package com.example.emtyapp.data.Repository

import com.example.emtyapp.data.Entities.Order

interface OrderRepository {
    suspend fun getUserOrders(userId: String): List<Order>
    suspend fun cancelOrder(orderId: String): Boolean
    suspend fun reorderItems(orderId: String): Order
}