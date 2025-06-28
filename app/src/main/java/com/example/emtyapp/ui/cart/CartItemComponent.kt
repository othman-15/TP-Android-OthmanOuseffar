package com.example.emtyapp.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.emtyapp.data.Entities.CartItem

@Composable
fun CartItemComponent(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = cartItem.product.imageUrl,
                    contentDescription = cartItem.product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))


            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartItem.product.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))


                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${cartItem.product.price}DH",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (cartItem.product.oldPrice > cartItem.product.price) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${cartItem.product.oldPrice}DH",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))


                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    OutlinedIconButton(
                        onClick = {
                            if (cartItem.quantity > 1) {
                                onQuantityChange(cartItem.quantity - 1)
                            }
                        },
                        modifier = Modifier.size(32.dp),
                        enabled = cartItem.quantity > 1
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Diminuer",
                            modifier = Modifier.size(16.dp)
                        )
                    }


                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .widthIn(min = 40.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = cartItem.quantity.toString(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }


                    OutlinedIconButton(
                        onClick = { onQuantityChange(cartItem.quantity + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Augmenter",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))


            Column(
                horizontalAlignment = Alignment.End
            ) {
                IconButton(
                    onClick = onRemove
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))


                Text(
                    text = "%.2fDH".format(cartItem.product.price * cartItem.quantity),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}