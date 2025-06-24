package com.example.emtyapp.ui.product.screens

import DetailsProductScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.emtyapp.ui.product.components.BottomBar
import com.example.emtyapp.ui.product.screens.*


@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomeScreen(navController) }
                composable("cart") { CartScreen() }
                composable("profile") { ProfileScreen() }
                composable("search") { SearchScreen() }
                composable("details/{productId}") { backStackEntry ->
                    val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
                    DetailsProductScreen(
                        productId = productId,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}