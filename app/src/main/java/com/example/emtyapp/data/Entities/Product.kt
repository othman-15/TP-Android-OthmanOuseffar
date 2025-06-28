package com.example.emtyapp.data.Entities

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val description: String? = null,
    val oldPrice: Double,
    val imageUrl: String,
    val categorie: String? = null,
    val coleur: String? = null,
    val stock: Int = 0


)


@Serializable
data class ProductsResponse(
    val products: List<Product>
)