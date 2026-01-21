package com.secondhand.shop.screens.products

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.secondhand.shop.model.Product
import com.secondhand.shop.repository.ProductRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String, // Receive the ID from the navigation
    onBack: () -> Unit,
    onChatClicked: () -> Unit
) {
    val ecoGreen = Color(0xFF4CAF50)
    val context = LocalContext.current
    var isFavorite by remember { mutableStateOf(false) }

    // --- State for Product Data ---
    var product by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // --- Fetch Product Data ---
    LaunchedEffect(productId) {
        ProductRepository.getProductById(
            productId = productId,
            onSuccess = { fetchedProduct ->
                product = fetchedProduct
                isLoading = false
            },
            onError = {
                isLoading = false
                Toast.makeText(context, "Failed to load product", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Text(
                        text = "Product Details",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ecoGreen)
            )
        },
        bottomBar = {
            if (product != null) {
                Surface(shadowElevation = 16.dp, color = Color.White) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { isFavorite = !isFavorite },
                            modifier = Modifier.height(54.dp).weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isFavorite) Color.Red else Color.Black
                            )
                        }

                        Button(
                            onClick = onChatClicked,
                            modifier = Modifier.height(54.dp).weight(2.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = ecoGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Chat with Seller", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ecoGreen)
            }
        } else if (product != null) {
            val currentProduct = product!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
            ) {
                // --- 1. Dynamic Hero Image ---
                Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                    AsyncImage(
                        model = currentProduct.imageUrl,
                        contentDescription = "Product Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Condition Badge
                    Surface(
                        modifier = Modifier.padding(16.dp).align(Alignment.BottomStart),
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = currentProduct.condition,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // --- 2. Info Section ---
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "$${currentProduct.price}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ecoGreen
                    )

                    Text(
                        text = currentProduct.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Text(text = "Phnom Penh, Cambodia", color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Default.AccessTime, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        // Note: You can format the timestamp here
                        Text(text = "Just now", color = Color.Gray, fontSize = 14.sp)
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 24.dp),
                        thickness = 1.dp,
                        color = Color(0xFFF0F0F0)
                    )

                    // --- 3. Seller Card ---
                    Text(text = "Seller Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF9F9F9))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Seller ID: ${currentProduct.sellerId.take(8)}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "Verified Seller • 5.0 ★", color = ecoGreen, fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = { /* Profile Navigation */ },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, ecoGreen),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("View Profile", fontSize = 12.sp, color = ecoGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- 4. Description Section ---
                    Text(text = "Description", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        text = currentProduct.description,
                        color = Color.DarkGray,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        } else {
            // Error State
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Product not found.")
            }
        }
    }
}