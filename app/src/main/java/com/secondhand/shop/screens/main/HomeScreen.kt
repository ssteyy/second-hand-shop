package com.secondhand.shop.screens.main

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.secondhand.shop.model.Product
import com.secondhand.shop.repository.ProductRepository

@Composable
fun HomeScreen(
    onProductClick: (String) -> Unit
) {
    val ecoGreen = Color(0xFF4CAF50)
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // 🔥 Fetch products from Firestore
    LaunchedEffect(Unit) {
        ProductRepository.fetchAllAvailableProducts(
            onSuccess = {
                products = it
                isLoading = false
            },
            onError = {
                Toast.makeText(context, "Error loading products", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        )
    }

    val filteredProducts = products.filter { product ->
        val matchesSearch = product.title.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || product.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFB))
    ) {

        // ---------------- SEARCH BAR ----------------
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
                placeholder = { Text("Search items...") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = ecoGreen) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ecoGreen,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5)
                )
            )
        }

        // ---------------- CATEGORY ----------------
        CategoryRow(selectedCategory) { selectedCategory = it }

        // ---------------- CONTENT ----------------
        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ecoGreen)
                }
            }
            filteredProducts.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No products available", color = Color.Gray)
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredProducts) { product ->
                        ProductItem(
                            product = product,
                            onClick = { onProductClick(product.id) },
                            onFavoriteClick = { prod, isFav ->
                                ProductRepository.toggleFavorite(
                                    prod,
                                    onSuccess = {
                                        Toast.makeText(
                                            context,
                                            if (isFav) "Added to favorites" else "Removed from favorites",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onError = { e ->
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryRow(
    selected: String,
    onSelect: (String) -> Unit
) {
    val categories = listOf("All", "Furniture", "Clothes", "Electronics", "Books")

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        categories.forEach { category ->
            val isSelected = selected == category
            Surface(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { onSelect(category) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(0xFF4CAF50) else Color.White,
                border = BorderStroke(1.dp, Color(0xFFDCDCDC))
            ) {
                Text(
                    text = category,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = if (isSelected) Color.White else Color.Black
                )
            }
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    onClick: (String) -> Unit,
    onFavoriteClick: (Product, Boolean) -> Unit
) {
    val ecoGreen = Color(0xFF4CAF50)
    val context = LocalContext.current

    var isFavorite by remember { mutableStateOf(false) }

    // Initialize favorite state
    LaunchedEffect(product) {
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        isFavorite = userId != null && product.favorites.contains(userId)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(product.id) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column {

            // IMAGE SECTION
            Box(modifier = Modifier.height(150.dp)) {

                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(product.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // FAVORITE BUTTON
                IconButton(
                    onClick = {
                        isFavorite = !isFavorite
                        onFavoriteClick(product, isFavorite)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.White.copy(alpha = 0.85f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray
                    )
                }

                // CONDITION BADGE
                Surface(
                    modifier = Modifier.align(Alignment.TopStart),
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(bottomEnd = 12.dp)
                ) {
                    Text(
                        text = product.condition,
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            // INFO SECTION
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$${product.price}",
                    color = ecoGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
