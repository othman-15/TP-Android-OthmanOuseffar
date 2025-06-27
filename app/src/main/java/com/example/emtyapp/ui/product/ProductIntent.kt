package com.example.emtyapp.ui.product

sealed class ProductIntent {
    object LoadProducts : ProductIntent()
    object RefreshProducts : ProductIntent()
    data class SearchProducts(val query: String) : ProductIntent()
}