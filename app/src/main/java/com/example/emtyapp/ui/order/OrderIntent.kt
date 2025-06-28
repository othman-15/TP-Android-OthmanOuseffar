package com.example.emtyapp.ui.order

sealed class OrderIntent {
    object LoadOrders : OrderIntent()
    object Refresh : OrderIntent()
    data class CancelOrder(val orderId: String) : OrderIntent()
    data class ReorderItems(val orderId: String) : OrderIntent()
    object ClearError : OrderIntent()
}