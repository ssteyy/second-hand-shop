package com.secondhand.shop.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.secondhand.shop.model.Product
import com.secondhand.shop.screens.products.ListingItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerProfileScreen(
    sellerName: String,
    sellerEmail: String,
    sellerImageUrl: String?,
    sellerProducts: List<Product>,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit
) {
    val ecoGreen = Color(0xFF4CAF50)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Seller Profile", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ecoGreen)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F8F8)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- HEADER SECTION ---
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Profile Photo
                    AsyncImage(
                        model = sellerImageUrl ?: "https://via.placeholder.com/150",
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Name
                    Text(
                        text = sellerName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    // Email
                    Text(
                        text = sellerEmail,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Listings Title
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Seller's Listings",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                }
            }

            // --- LIST OF PRODUCTS ---
            if (sellerProducts.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("This seller has no active listings.", color = Color.Gray)
                    }
                }
            } else {
                items(sellerProducts) { product ->
                    // Reusing your ListingItemCard for consistency
                    ListingItemCard(
                        product = product,
                        ecoGreen = ecoGreen,
                        onEdit = {}, // Empty since a viewer cannot edit a seller's product
                        onProductClick = onProductClick,
                        onDeleteSuccess = {} // Empty for public view
                    )
                }
            }
        }
    }
}