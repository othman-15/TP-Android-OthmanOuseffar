package com.example.emtyapp.ui.product.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.emtyapp.ui.cart.CartIntent
import com.example.emtyapp.ui.cart.CartIntent.*
import com.example.emtyapp.ui.cart.CartItemComponent
import com.example.emtyapp.ui.cart.CartViewModel
import com.example.emtyapp.ui.cart.CartViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.handleIntent(CartIntent.LoadCart)
    }

    // Clear Cart Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Vider le panier") },
            text = { Text("Êtes-vous sûr de vouloir vider complètement votre panier ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        viewModel.handleIntent(CartIntent.ClearCart)
                    }
                ) {
                    Text("Confirmer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mon Panier") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    if (state is CartViewState.CartLoaded && (state as CartViewState.CartLoaded).items.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Vider le panier",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = state,
            transitionSpec = {
                slideInVertically { it } + fadeIn() togetherWith
                        slideOutVertically { -it } + fadeOut()
            }
        ) { currentState ->
            when (currentState) {
                is CartViewState.Loading -> {
                    LoadingContent(
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                is CartViewState.EmptyCart -> {
                    EmptyCartContent(
                        onContinueShopping = { navController.popBackStack() },
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                is CartViewState.CartLoaded -> {
                    CartContent(
                        items = currentState.items,
                        totalPrice = currentState.total,
                        itemsCount = currentState.items.count(),
                        onQuantityChange = { cartItemId, quantity ->
                            viewModel.handleIntent(UpdateQuantity(cartItemId, quantity))
                        },
                        onRemoveItem = { cartItemId ->
                            viewModel.handleIntent(RemoveItem(cartItemId))
                        },
                        onCheckout = {
                            viewModel.handleIntent(CartIntent.Checkout)
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                is CartViewState.Error -> {
                    ErrorContent(
                        message = currentState.message,
                        onRetry = { viewModel.handleIntent(CartIntent.LoadCart) },
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                is CartViewState.CheckoutSuccess -> {
                    CheckoutSuccessContent(
                        onContinueShopping = { navController.popBackStack() },
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                is CartViewState.NotLoggedIn -> {
                    NotLoggedInContent(
                        onLogin = {

                            navController.navigate("profile")
                        },
                        onBack = { navController.popBackStack() },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
private fun CartContent(
    items: List<com.example.emtyapp.data.Entities.CartItem>,
    totalPrice: Double,
    itemsCount: Int,
    onQuantityChange: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {

            items(items, key = { it.id }) { cartItem ->
                CartItemComponent(
                    cartItem = cartItem,
                    onQuantityChange = { quantity ->
                        onQuantityChange(cartItem.id, quantity)
                    },
                    onRemove = { onRemoveItem(cartItem.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$itemsCount article${if (itemsCount > 1) "s" else ""}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Total: ${totalPrice}DH",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onCheckout,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Commander maintenant",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCartContent(
    onContinueShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Votre panier est vide",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Découvrez nos produits et ajoutez-les à votre panier",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onContinueShopping,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Continuer mes achats",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun NotLoggedInContent(
    onLogin: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Connexion requise",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Vous devez être connecté pour accéder à votre panier",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onLogin,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Se connecter",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onBack,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Retour",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Chargement du panier...")
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Erreur",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("Réessayer")
        }
    }
}

@Composable
private fun CheckoutSuccessContent(
    onContinueShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎉",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Commande réussie !",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Votre commande a été passée avec succès",
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onContinueShopping) {
            Text("Continuer mes achats")
        }
    }
}