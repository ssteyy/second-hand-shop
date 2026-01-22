package com.secondhand.shop.screens.main

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onProductClick: (String) -> Unit // 1. Correct: pass productId
) {
    val ecoGreen = Color(0xFF4CAF50)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        // 1. Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp)
                .background(Color.White, RoundedCornerShape(25.dp))
                .clickable { onNavigateToSearch() }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Search for eco-friendly deals...", color = Color.Gray, fontSize = 14.sp)
            }
        }

        // 2. Categories Row
        Text(
            text = "Categories",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        CategoryRow(ecoGreen)

        // 3. Product Grid
        Text(
            text = "Fresh Finds",
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        ProductGrid(onProductClick = onProductClick)
    }
}

@Composable
fun CategoryRow(ecoGreen: Color) {
    val categories = listOf("All", "Furniture", "Clothes", "Electronics", "Books")
    var selectedCategory by remember { mutableStateOf("All") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        categories.forEach { category ->
            val isSelected = selectedCategory == category
            Button(
                onClick = { selectedCategory = category },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) ecoGreen else Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(2.dp),
                modifier = Modifier.padding(end = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(
                    text = category,
                    color = if (isSelected) Color.White else Color.Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ProductGrid(onProductClick: (String) -> Unit) {
    val dummyProducts = List(10) { index -> "product_$index" }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(dummyProducts) { productId ->
            ProductItem(productId = productId, onClick = onProductClick)
        }
    }
}

@Composable
fun ProductItem(productId: String, onClick: (String) -> Unit) {
    val ecoGreen = Color(0xFF4CAF50)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(productId) },
        shape = RoundedCornerShape(20.dp), // Matched FavoritesScreen
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column {
            // --- Image Section ---
            Box(modifier = Modifier.height(160.dp).fillMaxWidth()) {
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF9F9F9)),
                    contentScale = ContentScale.Fit
                )

                // Condition Badge (Matches Favorites)
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(topStart = 0.dp, bottomEnd = 12.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "Used - Like New", // You can pass this via data later
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // --- Info Section ---
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Second Hand Chair",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$25.00",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = ecoGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                // View Details Button (Matches Favorites)
                Button(
                    onClick = { onClick(productId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
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