import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.emtyapp.data.Entities.Product
import com.example.emtyapp.ui.product.ProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsProductScreen(
    productId: String,
    viewModel: ProductViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    var productState by remember { mutableStateOf<Product?>(null) }
    var errorState by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isFavorite by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf(1) }
    var isAddingToCart by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        try {
            isLoading = true
            val product = viewModel.getProductById(productId)
            productState = product
        } catch (e: Exception) {
            errorState = "Erreur de chargement du produit"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Détails du produit",
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {  }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Partager"
                        )
                    }
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favoris",
                            tint = if (isFavorite) Color.Red else LocalContentColor.current
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            if (productState != null) {
                BottomActionBar(
                    product = productState!!,
                    quantity = quantity,
                    isAddingToCart = isAddingToCart,
                    onQuantityChange = { quantity = it },
                    onAddToCart = {
                        isAddingToCart = true

                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                            kotlinx.coroutines.delay(1500)
                            isAddingToCart = false
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = when {
                    isLoading -> "loading"
                    errorState != null -> "error"
                    productState != null -> "content"
                    else -> "loading"
                },
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { state ->
                when (state) {
                    "loading" -> LoadingContent()
                    "error" -> ErrorContent(message = errorState ?: "Erreur inconnue")
                    "content" -> ProductDetailsContent(
                        product = productState!!,
                        isFavorite = isFavorite
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductDetailsContent(
    product: Product,
    isFavorite: Boolean
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {

        ImageGallery(product = product)


        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            ProductHeader(product = product)

            Spacer(modifier = Modifier.height(16.dp))


            PriceSection(product = product)

            Spacer(modifier = Modifier.height(20.dp))


            RatingSection()

            Spacer(modifier = Modifier.height(20.dp))


            DescriptionSection(product = product)

            Spacer(modifier = Modifier.height(20.dp))


            SpecificationSection()

            Spacer(modifier = Modifier.height(100.dp)) // Espace pour la bottom bar
        }
    }
}

@Composable
private fun ImageGallery(product: Product) {

    val images = listOf(
        product.imageResId,
        product.imageResId,
        product.imageResId
    )
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = product.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }


        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(images.size) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index)
                                Color.White
                            else
                                Color.White.copy(alpha = 0.5f)
                        )
                )
            }
        }


        if (product.oldPrice > product.price) {
            val discount = ((product.oldPrice - product.price) / product.oldPrice * 100).toInt()
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFFFF5722), Color(0xFFD32F2F))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "-$discount%",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ProductHeader(product: Product) {
    Column {
        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Marque Premium",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun PriceSection(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${product.price} DH",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFFD32F2F)
                )

                if (product.oldPrice > product.price) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${product.oldPrice} DH",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textDecoration = TextDecoration.LineThrough,
                                color = Color(0xFF757575)
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        val savings = product.oldPrice - product.price
                        Text(
                            text = "Économisez $savings DH",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }


            Box(
                modifier = Modifier
                    .background(
                        Color(0xFF4CAF50),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Meilleur prix",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun RatingSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Étoiles
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < 4) Color(0xFFFFC107) else Color(0xFFE0E0E0),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "4.5",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Nombre d'avis
            TextButton(onClick = {  }) {
                Text("127 avis")
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun DescriptionSection(product: Product) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            val description = product.description ?: "Description détaillée du produit avec toutes ses caractéristiques et avantages. Ce produit de qualité premium vous offre une expérience exceptionnelle."

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (!isExpanded) {
                Text(
                    text = description.take(100) + "...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            TextButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(if (isExpanded) "Voir moins" else "Voir plus")
            }
        }
    }
}

@Composable
private fun SpecificationSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Spécifications",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            val specs = listOf(
                "Couleur" to "Noir",
                "Matériau" to "Premium",
                "Garantie" to "2 ans",
                "Livraison" to "Gratuite"
            )

            specs.forEach { (key, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = key,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (specs.last() != (key to value)) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomActionBar(
    product: Product,
    quantity: Int,
    isAddingToCart: Boolean,
    onQuantityChange: (Int) -> Unit,
    onAddToCart: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
                        enabled = quantity > 1
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Diminuer")
                    }

                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = { onQuantityChange(quantity + 1) }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Augmenter")
                    }
                }
            }


            Button(
                onClick = onAddToCart,
                modifier = Modifier.weight(1f),
                enabled = !isAddingToCart,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isAddingToCart) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ajout...")
                } else {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Ajouter • ${product.price * quantity} DH",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Chargement du produit...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ErrorContent(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Red
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Red,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {  }
            ) {
                Text("Réessayer")
            }
        }
    }
}