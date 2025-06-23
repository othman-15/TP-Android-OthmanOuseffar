package com.example.emtyapp.ui.product.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.emtyapp.ui.product.ProductIntent
import com.example.emtyapp.ui.product.ProductViewModel
import com.example.emtyapp.ui.product.ProductViewState
import com.example.emtyapp.ui.product.components.ProductsComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ProductViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(ProductIntent.LoadProducts)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nos Produits") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        when (state) {
            is ProductViewState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is ProductViewState.ProductsLoaded -> {
                ProductsComponent(
                    products = (state as ProductViewState.ProductsLoaded).products,
                    onProductClick = { productId ->
                        navController.navigate("details/$productId")
                    },
                    onAddToCart = { product ->
                        // TODO: Intégrer panier ici (par ex. viewModel.addToCart(product))
                        println("Ajouté au panier : ${product.name}")
                    },
                    modifier = Modifier.padding(padding)
                )
            }

            is ProductViewState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Erreur : ${(state as ProductViewState.Error).message}", color = Color.Red)
                }
            }
        }
    }
}
