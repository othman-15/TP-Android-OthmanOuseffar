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


@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("cart") {
            CartScreen(navController = navController)
        }

        composable("profile") {
            ProfileScreen()
        }
        composable(
            "details/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable

            DetailsProductScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}