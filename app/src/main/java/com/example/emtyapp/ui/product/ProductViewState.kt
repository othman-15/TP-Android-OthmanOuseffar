package com.example.emtyapp.ui.product

import com.example.emtyapp.data.Entities.Product

sealed class ProductViewState {
    object Loading : ProductViewState()
    data class ProductsLoaded(val products: List<Product>) : ProductViewState()
    data class Error(val message: String) : ProductViewState()
}