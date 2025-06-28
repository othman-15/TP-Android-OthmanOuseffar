package com.example.emtyapp.nav

import DetailsProductScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.emtyapp.ui.product.screens.CartScreen
import com.example.emtyapp.ui.product.screens.HomeScreen
import com.example.emtyapp.ui.product.screens.ProfileScreen
import com.example.emtyapp.ui.order.OrdersScreen

import com.example.emtyapp.ui.product.screens.SearchScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController = navController)
        }

        composable("cart") {
            CartScreen(navController = navController)
        }

        composable("profile") {
            ProfileScreen()
        }
        composable("orders") {
            OrdersScreen(
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


        composable(
            route = "details/{productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")

            if (productId != null) {
                DetailsProductScreen(
                    productId = productId,
                    onBackClick = {
                        try {
                            navController.popBackStack()
                        } catch (e: Exception) {
                            println("Error navigating back from details: ${e.message}")
                            navController.navigate("home") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}