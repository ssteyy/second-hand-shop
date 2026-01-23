package com.secondhand.shop.screens.products

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue
import com.secondhand.shop.model.Product
import com.secondhand.shop.repository.ProductRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBack: () -> Unit,
    onChatClicked: (String, String) -> Unit,
    onViewProfile: (String) -> Unit,
    onManageListings: () -> Unit,
) {
    val ecoGreen = Color(0xFF4CAF50)
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUserId = remember { auth.currentUser?.uid }
    val db = FirebaseFirestore.getInstance()

    // --- State ---
    var product by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isFavorite by remember { mutableStateOf(false) }
    var sellerName by remember { mutableStateOf("Loading...") }
    var sellerProfileImageUrl by remember { mutableStateOf<String?>(null) }

    // --- Helper: Send Like Notification ---
    fun sendLikeNotification(sellerId: String, productTitle: String) {
        val currentUserName = auth.currentUser?.displayName ?: "Someone"
        val notificationData = hashMapOf(
            "userId" to sellerId,
            "title" to "New Like!",
            "body" to "$currentUserName liked your listing '$productTitle'",
            "type" to "like",
            "timestamp" to Timestamp.now()
        )
        db.collection("notifications").add(notificationData)
            .addOnFailureListener { e -> Log.e("NotificationError", "Failed: ${e.message}") }
    }

    // --- Fetch Product & Seller Data ---
    LaunchedEffect(productId) {
        ProductRepository.getProductById(
            productId,
            onSuccess = { fetchedProduct ->
                product = fetchedProduct
                isFavorite = currentUserId != null && fetchedProduct?.favorites?.contains(currentUserId) == true

                fetchedProduct?.sellerId?.let { id ->
                    db.collection("users").document(id).get()
                        .addOnSuccessListener { snapshot ->
                            sellerName = snapshot.getString("fullName") ?: snapshot.getString("username") ?: "Seller"
                            sellerProfileImageUrl = snapshot.getString("profileImage")
                        }
                }
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
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = { Text("Product Details", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ecoGreen)
            )
        },
        bottomBar = {
            product?.let { currentProduct ->
                val isOwner = currentProduct.sellerId == currentUserId
                Surface(shadowElevation = 16.dp, color = Color.White) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // --- Favorite Toggle with Notification Trigger ---
                        OutlinedButton(
                            onClick = {
                                if (currentUserId == null) {
                                    Toast.makeText(context, "Login to favorite", Toast.LENGTH_SHORT).show()
                                    return@OutlinedButton
                                }

                                val productRef = db.collection("products").document(productId)
                                val wasFavorite = isFavorite
                                isFavorite = !wasFavorite

                                if (!wasFavorite) {
                                    // Add favorite and send notification
                                    productRef.update("favorites", FieldValue.arrayUnion(currentUserId))
                                        .addOnSuccessListener {
                                            // ✅ Trigger Notification to Seller
                                            sendLikeNotification(currentProduct.sellerId, currentProduct.title)
                                        }
                                        .addOnFailureListener { isFavorite = wasFavorite }
                                } else {
                                    // Remove favorite
                                    productRef.update("favorites", FieldValue.arrayRemove(currentUserId))
                                        .addOnFailureListener { isFavorite = wasFavorite }
                                }
                            },
                            modifier = Modifier.height(54.dp).weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (isFavorite) Color.Red else Color.LightGray)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isFavorite) Color.Red else Color.Black
                            )
                        }

                        if (!isOwner) {
                            Button(
                                onClick = {
                                    if (currentUserId == null) return@Button
                                    val ids = listOf(currentUserId, currentProduct.sellerId).sorted()
                                    val combinedChatId = "${ids[0]}_${ids[1]}"
                                    val chatData = hashMapOf(
                                        "members" to ids,
                                        "memberNames" to mapOf(
                                            currentUserId to "Buyer",
                                            currentProduct.sellerId to sellerName
                                        ),
                                        "lastMessage" to "Hi, is '${currentProduct.title}' still available?",
                                        "lastMessageTime" to Timestamp.now(),
                                        // ✅ Add unread count for seller to see the message notification
                                        "unreadCounts.${currentProduct.sellerId}" to 1
                                    )

                                    db.collection("chats").document(combinedChatId)
                                        .set(chatData, SetOptions.merge())
                                        .addOnSuccessListener { onChatClicked(combinedChatId, sellerName) }
                                },
                                modifier = Modifier.height(54.dp).weight(2.5f),
                                colors = ButtonDefaults.buttonColors(containerColor = ecoGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Chat with Seller", fontWeight = FontWeight.Bold)
                            }
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
        } else product?.let { currentProduct ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = currentProduct.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(350.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(20.dp)) {
                    Text("$${currentProduct.price}", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = ecoGreen)
                    Text(currentProduct.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Text("Phnom Penh", color = Color.Gray, fontSize = 14.sp)
                    }

                    Divider(modifier = Modifier.padding(vertical = 24.dp), color = Color(0xFFF0F0F0))

                    // --- Seller Info ---
                    Text("Seller Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF9F9F9))
                            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0xFFE0E0E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (sellerProfileImageUrl.isNullOrEmpty()) {
                                Text(sellerName.take(1).uppercase(), fontWeight = FontWeight.Bold, color = Color.Gray)
                            } else {
                                AsyncImage(
                                    model = sellerProfileImageUrl,
                                    contentDescription = "Seller Profile",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = sellerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Verified Member", fontSize = 12.sp, color = ecoGreen)
                        }

                        OutlinedButton(
                            onClick = {
                                if (currentProduct.sellerId == currentUserId) onManageListings()
                                else onViewProfile(currentProduct.sellerId)
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, ecoGreen)
                        ) {
                            Text(
                                if (currentProduct.sellerId == currentUserId) "My Ads" else "Profile",
                                fontSize = 12.sp, color = ecoGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Description", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        currentProduct.description,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 8.dp),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}