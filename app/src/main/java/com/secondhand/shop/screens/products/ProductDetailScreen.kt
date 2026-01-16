package com.secondhand.shop.screens.products

import android.R
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProductDetailScreen(onBack: () -> Unit, onChatClicked: () -> Unit) {
    val ecoGreen = Color(0xFF4CAF50)
    var isFavorite by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp) // Space for the floating bottom bar
        ) {
            // --- 1. Header Image & Back Button ---
            Box(modifier = Modifier.fillMaxWidth().height(380.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_menu_gallery), // Ensure this exists in res/drawable
                    contentDescription = "Product Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Back Button with semi-transparent background
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }

            // --- 2. Product Content ---
            Column(modifier = Modifier.padding(20.dp)) {
                // Price and Heart Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$250.00",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ecoGreen
                    )
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Text(
                    text = "Premium Leather Camera Bag",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Text(text = "Phnom Penh, Cambodia", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.AccessTime, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Text(text = "2 hours ago", color = Color.Gray, fontSize = 14.sp)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp), thickness = 1.dp, color = Color(0xFFF0F0F0))

                // --- 3. Seller Info Card ---
                Text(text = "Seller", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF9F9F9))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Sok Nimol", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Verified Seller", color = ecoGreen, fontSize = 12.sp)
                    }
                    OutlinedButton(onClick = { /* View Profile */ }) {
                        Text("View Profile", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- 4. Description ---
                Text(text = "Description", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = "High-quality vintage camera bag made from authentic leather. It features three internal compartments and adjustable straps. Very minor wear on the buckle, otherwise perfect condition.",
                    color = Color.DarkGray,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // --- 5. Floating Action Bottom Bar ---
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter),
            shadowElevation = 20.dp,
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Secondary Action: Add to Favorite
                OutlinedButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier.height(56.dp).weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) Color.Red else Color.Black
                    )
                }

                // Primary Action: Chat
                Button(
                    onClick = onChatClicked,
                    modifier = Modifier.height(56.dp).weight(2.5f),
                    colors = ButtonDefaults.buttonColors(containerColor = ecoGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chat with Seller", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}