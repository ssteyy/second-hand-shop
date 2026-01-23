package com.secondhand.shop.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    sellerBio: String?,
    sellerPhone: String?,
    sellerImageUrl: String?,
    sellerProducts: List<Product>,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    onChatClick: (String, String) -> Unit // ✅ Added: (sellerId, sellerName)
) {
    val ecoGreen = Color(0xFF4CAF50)
    val activeListings = remember(sellerProducts) { sellerProducts.filter { !it.sold } }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Seller Profile", fontWeight = FontWeight.Bold, color = Color.White) },
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
            modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF8F8F8)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- SELLER HEADER ---
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = sellerImageUrl ?: "https://via.placeholder.com/150",
                            contentDescription = null,
                            modifier = Modifier.size(90.dp).clip(CircleShape).background(Color(0xFFF5F5F5)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(sellerName, fontSize = 22.sp, fontWeight = FontWeight.Bold)

                        if (!sellerBio.isNullOrBlank()) {
                            Text(
                                text = sellerBio,
                                fontSize = 14.sp, color = Color.DarkGray, textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                            Spacer(Modifier.width(8.dp))
                            Text(sellerEmail, fontSize = 13.sp, color = Color.Gray)
                        }

                        if (!sellerPhone.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                Spacer(Modifier.width(8.dp))
                                Text(sellerPhone, fontSize = 13.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            item {
                Text("Items for Sale", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            // --- LIST OF PRODUCTS ---
            items(activeListings) { product ->
                ListingItemCard(
                    product = product,
                    ecoGreen = ecoGreen,
                    onProductClick = { onProductClick(product.id) },
                    onEdit = {},
                    onDeleteSuccess = {},
                    showOptions = false,
                    onChatClick = {
                        // ✅ Pass the seller info back up to navigate to chat
                        onChatClick(product.sellerId, sellerName)
                    }
                )
            }
        }
    }
}