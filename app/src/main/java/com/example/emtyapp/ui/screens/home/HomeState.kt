package com.example.emtyapp.ui.screens.home

import com.example.emtyapp.data.Entities.Product

data class HomeState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null
)
