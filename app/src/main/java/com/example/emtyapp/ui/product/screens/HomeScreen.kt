package com.example.emtyapp.ui.product.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.emtyapp.ui.cart.CartIntent
import com.example.emtyapp.ui.cart.CartViewModel
import com.example.emtyapp.ui.product.ProductIntent
import com.example.emtyapp.ui.product.ProductViewModel
import com.example.emtyapp.ui.product.ProductViewState
import com.example.emtyapp.ui.product.components.ProductsComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ProductViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.handleIntent(ProductIntent.LoadProducts)
    }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchTopBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearchClose = {
                        isSearchActive = false
                        searchQuery = ""

                        viewModel.handleIntent(ProductIntent.LoadProducts)
                    },
                    onSearch = { query ->
                        viewModel.handleIntent(ProductIntent.SearchProducts(query))
                    }
                )
            } else {
                MainTopBar(
                    onSearchClick = { isSearchActive = true },
                    onNotificationClick = {  },
                    onCartClick = { navController.navigate("cart") }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            WelcomeBanner()

            AnimatedContent(
                targetState = state,
                transitionSpec = {
                    slideInVertically { it } + fadeIn() togetherWith
                            slideOutVertically { -it } + fadeOut()
                },
                label = "content_animation"
            ) { currentState ->
                when (currentState) {
                    is ProductViewState.Loading -> {
                        LoadingContent()
                    }

                    is ProductViewState.ProductsLoaded -> {
                        ProductsSection(
                            products = currentState.products,
                            onProductClick = { productId ->
                                navController.navigate("details/$productId")
                            },
                            onAddToCart = { product ->
                                cartViewModel.handleIntent(CartIntent.AddToCart(product))
                            },
                            isSearchActive = isSearchActive,
                            searchQuery = searchQuery
                        )
                    }

                    is ProductViewState.Error -> {
                        ErrorContent(
                            message = currentState.message,
                            onRetry = {
                                viewModel.handleIntent(ProductIntent.LoadProducts)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onCartClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Bonjour ! 👋",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
                Text(
                    text = "Découvrez nos produits",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Rechercher",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClose: () -> Unit,
    onSearch: (String) -> Unit
) {
    TopAppBar(
        title = {
            OutlinedTextField(
                value = query,
                onValueChange = { newQuery ->
                    onQueryChange(newQuery)

                    onSearch(newQuery)
                },
                placeholder = {
                    Text(
                        "Rechercher un produit...",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = onSearchClose) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Fermer recherche",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun WelcomeBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "🎉 Offres Spéciales",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Jusqu'à -50% sur une sélection de produits",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = {  },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "Voir les offres →",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductsSection(
    products: List<com.example.emtyapp.data.Entities.Product>,
    onProductClick: (String) -> Unit,
    onAddToCart: (com.example.emtyapp.data.Entities.Product) -> Unit,
    isSearchActive: Boolean = false,
    searchQuery: String = ""
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isSearchActive && searchQuery.isNotBlank()) {
                    "Résultats de recherche"
                } else {
                    "Nos Produits"
                },
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            if (!isSearchActive) {
                TextButton(onClick = {  }) {
                    Text("Voir tout")
                }
            }
        }

        Text(
            text = if (isSearchActive && searchQuery.isNotBlank()) {
                "${products.size} résultat(s) pour \"$searchQuery\""
            } else {
                "${products.size} produits disponibles"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        if (products.isEmpty() && isSearchActive && searchQuery.isNotBlank()) {
            EmptySearchResults(searchQuery = searchQuery)
        } else {
            ProductsComponent(
                products = products,
                onProductClick = onProductClick,
                onAddToCart = onAddToCart,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun EmptySearchResults(
    searchQuery: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔍",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Aucun résultat trouvé",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Aucun produit ne correspond à \"$searchQuery\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.primary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(60.dp),
                    strokeWidth = 4.dp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Chargement des produits...",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Nous préparons le meilleur pour vous",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Une erreur s'est produite",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        "Réessayer",
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = {  }) {
                    Text("Contacter le support")
                }
            }
        }
    }
}