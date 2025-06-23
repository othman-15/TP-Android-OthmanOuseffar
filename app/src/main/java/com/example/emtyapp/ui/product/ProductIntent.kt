package com.example.emtyapp.ui.product

sealed class ProductIntent {
    object LoadProducts : ProductIntent()
    data class SelectProduct(val productId: String) : ProductIntent()
}
