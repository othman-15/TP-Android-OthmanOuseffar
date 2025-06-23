package com.example.emtyapp.data.Repository
import com.example.emtyapp.R
import com.example.emtyapp.data.Entities.Product
class ProductRepository {
    fun getProducts(): List<Product> =listOf(
        Product("PR001", "Hp Revolution 6", 379.0,"zd", 619.0, imageResId = R.drawable.a,"pc"),
        Product("PR002", "Robot de Cuisine", 699.0,"zd", 900.0, R.drawable.b,"pc"),
        Product("PR003", "Hp-Pavilon", 219.0,"zd", 272.0, R.drawable.c,"pc"),
        Product("PR006", "Hp-impriment", 379.0,"zd", 619.0, imageResId = R.drawable.d,"pc"),
        Product("PR007", "Mac-book air", 699.0,"zd", 900.0, R.drawable.emac,"pc"),
        Product("PR008", "Hp-mouse", 219.0,"zd", 272.0, R.drawable.fmouse,"pc"),

        )
}
