package com.example.emtyapp.data.Entities

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val description: String? = null,
    val oldPrice: Double,
    val imageResId: Int,
    val categorie: String? = null,

)