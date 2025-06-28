package com.example.emtyapp.ui.order

import com.example.emtyapp.data.Entities.Order

data class OrderState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val isEmpty: Boolean = false
)