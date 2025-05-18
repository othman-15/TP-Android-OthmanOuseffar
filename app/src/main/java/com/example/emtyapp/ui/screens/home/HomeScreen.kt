package com.example.emtyapp.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emtyapp.ui.components.ProductCard

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToDetails: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.handleIntent(HomeIntent.LoadProducts)
    }

    Column(modifier = Modifier.padding(8.dp)) {
        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }
            state.error != null -> {
                Text(text = "Erreur : ${state.error}")
            }
            else -> {
                LazyColumn {
                    items(state.products) { product ->
                        ProductCard(product = product) {
                            onNavigateToDetails(product.id)
                        }
                    }
                }
            }
        }
    }
}
