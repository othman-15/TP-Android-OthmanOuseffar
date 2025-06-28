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
import com.example.emtyapp.ui.order.OrdersScreen
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
                composable("cart") { CartScreen(navController) }
                composable("profile") { ProfileScreen() }
                composable("orders") {
                    OrdersScreen(
                        // Le ViewModel sera injecté automatiquement avec Hilt
                        // Pas besoin de le passer explicitement si vous utilisez hiltViewModel() dans OrdersScreen
                        onBackClick = {
                            try {
                                navController.popBackStack()
                            } catch (e: Exception) {
                                println("Error navigating back from orders: ${e.message}")
                                // En cas d'erreur, retourner à l'écran d'accueil
                                navController.navigate("home") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    )
                }
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