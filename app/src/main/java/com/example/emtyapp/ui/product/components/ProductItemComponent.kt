package com.example.emtyapp.ui.product.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emtyapp.data.Entities.Product
import kotlinx.coroutines.launch

@Composable
fun ProductItemComponent(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isAddingToCart by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Image(
                    painter = painterResource(id = product.imageResId),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )


                if (product.oldPrice > product.price) {
                    val discount = ((product.oldPrice - product.price) / product.oldPrice * 100).toInt()
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFFF5722), Color(0xFFD32F2F))
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "-$discount%",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))


            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < 4) Color(0xFFFFC107) else Color(0xFFE0E0E0),
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "4.2",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${product.price} DH",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFFD32F2F)
                )

                if (product.oldPrice > product.price) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${product.oldPrice} DH",
                        style = MaterialTheme.typography.bodySmall.copy(
                            textDecoration = TextDecoration.LineThrough,
                            color = Color(0xFF757575)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))


            FilledTonalButton(
                onClick = {
                    isAddingToCart = true
                    onAddToCart()

                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                        kotlinx.coroutines.delay(1000)
                        isAddingToCart = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                enabled = !isAddingToCart
            ) {
                if (isAddingToCart) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ajout en cours...")
                } else {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ajouter au panier",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}


