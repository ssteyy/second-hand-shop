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
fun ProductGrid(onProductClick: (String) -> Unit) { // 2. Accept productId
    val dummyProducts = List(10) { index -> "product_$index" } // dummy product IDs

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(dummyProducts) { productId ->
            ProductItem(productId = productId, onClick = onProductClick)
        }
    }
}

@Composable
fun ProductItem(productId: String, onClick: (String) -> Unit) { // 3. Pass productId
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(productId) }, // 4. Pass id to lambda
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color.LightGray)
            ) {
                Text("Image", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Second Hand Chair",
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$25.00",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Used - Like New",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
