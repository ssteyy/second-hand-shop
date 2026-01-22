package com.secondhand.shop.screens.main

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secondhand.shop.screens.products.FavoriteProduct

@Composable
fun HomeScreen(
    onProductClick: (String) -> Unit
) {
    val ecoGreen = Color(0xFF4CAF50)
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    // Dummy Data with Category info added
    val allProducts = remember {
        listOf(
            FavoriteProduct("1", "Vintage Denim Jacket", "$45.00", "Used"),
            FavoriteProduct("2", "Modern Coffee Table", "$120.00", "New"),
            FavoriteProduct("3", "Wireless Headphones", "$85.00", "Like New"),
            FavoriteProduct("4", "Designer Backpack", "$210.00", "Used"),
            FavoriteProduct("5", "Potted Plant", "$30.00", "Fresh"),
            FavoriteProduct("6", "Retro Camera", "$99.00", "Used"),
            FavoriteProduct("7", "Wooden Chair", "$55.00", "Used"),
            FavoriteProduct("8", "Desk Lamp", "$15.00", "Like New")
        )
    }

    val filteredProducts = allProducts.filter { product ->
        val matchesSearch = product.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || product.name.contains(selectedCategory, ignoreCase = true)
        matchesSearch && matchesCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFB)) // Slightly softer white
    ) {
        // --- Search Bar Section ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search items...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ecoGreen) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedBorderColor = ecoGreen,
                    unfocusedBorderColor = Color.Transparent
                )
            )
        }

        // --- Categories ---
        Column(modifier = Modifier.padding(top = 16.dp)) {
            Text(
                text = "Browse Categories",
                modifier = Modifier.padding(horizontal = 20.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            CategoryRow(selectedCategory, { selectedCategory = it }, ecoGreen)
        }

        // --- Product Grid ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (searchQuery.isEmpty()) "Featured Items" else "Results Found",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${filteredProducts.size} items",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        if (filteredProducts.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No items found matching your filter.", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProducts) { product ->
                    EnhancedProductItem(product, onProductClick)
                }
            }
        }
    }
}

@Composable
fun CategoryRow(selected: String, onSelect: (String) -> Unit, color: Color) {
    val categories = listOf("All", "Furniture", "Clothes", "Electronics", "Books")
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        categories.forEach { cat ->
            val isSel = selected == cat
            Surface(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { onSelect(cat) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSel) color else Color.White,
                border = BorderStroke(1.dp, if (isSel) color else Color(0xFFE0E0E0)),
                shadowElevation = if (isSel) 4.dp else 0.dp
            ) {
                Text(
                    text = cat,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = if (isSel) Color.White else Color.DarkGray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun EnhancedProductItem(product: FavoriteProduct, onClick: (String) -> Unit) {
    val ecoGreen = Color(0xFF4CAF50)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(product.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(160.dp)) {
                // Image Placeholder
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().background(Color(0xFFF0F0F0)),
                    contentScale = ContentScale.Inside
                )

                // Condition Badge
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(bottomEnd = 12.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = product.condition,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = product.price,
                        style = MaterialTheme.typography.titleMedium,
                        color = ecoGreen,
                        fontWeight = FontWeight.ExtraBold
                    )

                    // Small Arrow Icon for a premium feel
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_send),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp).clip(CircleShape),
                        tint = Color.LightGray
                    )
                }
            }
        }
    }
}