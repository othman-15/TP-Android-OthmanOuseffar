package com.example.emtyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.emtyapp.data.sampleProducts
import com.example.emtyapp.ui.components.ProductCard

@Composable
fun HomeScreen(onNavigateToDetails: (String) -> Unit) {
    Column(modifier = Modifier.padding(0.1.dp)) {


        LazyColumn {
            items(sampleProducts) { product ->
                ProductCard(product = product) {
                    onNavigateToDetails(product.id)
                }
            }
        }
    }
}
