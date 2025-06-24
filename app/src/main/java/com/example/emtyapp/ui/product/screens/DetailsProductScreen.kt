import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emtyapp.data.Entities.Product
import com.example.emtyapp.data.Repository.ProductRepository
import com.example.emtyapp.ui.product.ProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsProductScreen(
    productId: String,
    viewModel: ProductViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val productState = remember { mutableStateOf<Product?>(null) }
    val errorState = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(productId) {
        try {
            val product = viewModel.getProductById(productId)
            productState.value = product
        } catch (e: Exception) {
            errorState.value = "Erreur de chargement"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détails du produit") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                productState.value != null -> {
                    ProductDetailsContent(product = productState.value!!)
                }
                errorState.value != null -> {
                    ErrorContent(message = errorState.value!!)
                }
                else -> {
                    LoadingContent()
                }
            }
        }
    }
}

@Composable
private fun ProductDetailsContent(product: Product) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            painter = painterResource(id = product.imageResId),
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        PriceRow(product = product)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = product.description ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PriceRow(product: Product) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "${product.price} DH",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFFD32F2F)
        )
        Spacer(modifier = Modifier.width(12.dp))
        if (product.oldPrice > product.price) {
            Text(
                text = "${product.oldPrice} DH",
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = TextDecoration.LineThrough,
                    color = Color.Gray
                )
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Chargement...")
    }
}

@Composable
private fun ErrorContent(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = Color.Red)
    }
}