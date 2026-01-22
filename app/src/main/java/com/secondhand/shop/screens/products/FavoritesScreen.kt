package com.secondhand.shop.screens.products

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data Class for Product items
data class FavoriteProduct(
    val id: String,
    val name: String,
    val price: String,
    val condition: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBack: () -> Unit, // 1. Added back navigation callback
    onProductClick: (String) -> Unit
) {
    val ecoGreen = Color(0xFF4CAF50)
    val white = Color.White

    // 1. Manage the list of favorites in a state
    val favoriteItems = remember {
        mutableStateListOf(
            FavoriteProduct("1", "Vintage Denim Jacket", "$45.00", "Used"),
            FavoriteProduct("2", "Modern Coffee Table", "$120.00", "New"),
            FavoriteProduct("3", "Wireless Headphones", "$85.00", "Like New"),
            FavoriteProduct("4", "Designer Backpack", "$210.00", "Used"),
            FavoriteProduct("5", "Potted Plant", "$30.00", "Fresh"),
            FavoriteProduct("6", "Retro Camera", "$99.00", "Used")
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Text(
                        text = "My Favorites",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = white,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = white
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors( // Match the colors
                    containerColor = ecoGreen
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Sub-header: Item Count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${favoriteItems.size} Items saved",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            if (favoriteItems.isEmpty()) {
                // Empty State
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No favorites yet", color = Color.Gray)
                    }
                }
            } else {
                // Grid Content
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Inside your LazyVerticalGrid
                    items(favoriteItems, key = { it.id }) { product ->
                        FavoriteProductCard(
                            product = product,
                            ecoGreen = ecoGreen,
                            onCardClick = { onProductClick(product.id) }, // This sends the ID back to MainScreen
                            onRemoveClick = { favoriteItems.remove(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteProductCard(
    product: FavoriteProduct,
    ecoGreen: Color,
    onCardClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
    ) {
        Column {
            Box(modifier = Modifier.height(160.dp)) {
                // Product Image Placeholder
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF9F9F9)),
                    contentScale = ContentScale.Fit
                )

                // "Remove" Heart Overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp,
                        modifier = Modifier.size(32.dp)
                    ) {
                        IconButton(onClick = onRemoveClick) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Unfavorite",
                                tint = Color(0xFFE91E63),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Condition Badge
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(topStart = 0.dp, bottomEnd = 12.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = product.condition,
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.price,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = ecoGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Clicking this button also navigates to detail
                Button(
                    onClick = onCardClick,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ecoGreen.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "View Details",
                        color = ecoGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}