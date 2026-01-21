package com.secondhand.shop.screens.products

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.secondhand.shop.model.Product
import com.secondhand.shop.repository.ProductRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageListingsScreen(
    onBack: () -> Unit,
    onEditProduct: (String) -> Unit,
    onProductClick: (String) -> Unit
) {
    val ecoGreen = Color(0xFF4CAF50)
    val context = LocalContext.current
    val view = LocalView.current

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Active", "Sold")

    var productList by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // --- Load products from Firestore ---
    fun loadData() {
        if (currentUserId.isEmpty()) return
        isLoading = true
        val isSoldTab = selectedTab == 1

        Log.d("ManageListings", "Loading products: userId=$currentUserId sold=$isSoldTab")

        ProductRepository.fetchUserProducts(
            currentUserId = currentUserId,
            soldStatus = isSoldTab, // ✅ match Firestore field
            onSuccess = { products ->
                Log.d("ManageListings", "Fetched products: ${products.size}")
                productList = products
                isLoading = false
            },
            onError = { e ->
                Log.e("ManageListings", "Fetch error: ${e.message}", e)
                isLoading = false
                Toast.makeText(context, "Error loading listings: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

    // Reload when tab changes
    LaunchedEffect(selectedTab, currentUserId) { loadData() }

    // Status bar color
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.statusBarColor = ecoGreen.toArgb()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Listings", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ecoGreen)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F8F8))
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .height(3.dp)
                            .background(ecoGreen, RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) ecoGreen else Color.Gray,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Content
            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ecoGreen)
                    }
                }
                productList.isEmpty() -> {
                    val emptyMessage = if (selectedTab == 0) "No active listings found" else "No sold items yet"
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(emptyMessage, color = Color.Gray, fontSize = 16.sp)
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(productList) { product ->
                            ListingItemCard(
                                product = product,
                                ecoGreen = ecoGreen,
                                onEdit = onEditProduct,
                                onProductClick = onProductClick,
                                onDeleteSuccess = { loadData() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListingItemCard(
    product: Product,
    ecoGreen: Color,
    onEdit: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onDeleteSuccess: () -> Unit
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Listing?") },
            text = { Text("Are you sure you want to delete '${product.title}'?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    ProductRepository.deleteProduct(
                        productId = product.id,
                        onSuccess = {
                            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
                            onDeleteSuccess()
                        },
                        onError = { e ->
                            Toast.makeText(context, "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }) { Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick(product.id) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.title,
                modifier = Modifier.size(85.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(product.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("$${product.price}", color = ecoGreen, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(product.condition, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))

                if (!product.sold) { // ✅ match Firestore 'sold'
                    Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.clickable { onEdit(product.id) }, verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp), tint = Color.Gray)
                            Spacer(Modifier.width(4.dp))
                            Text("Edit", fontSize = 13.sp, color = Color.Gray)
                        }
                        Row(modifier = Modifier.clickable { showDeleteDialog = true }, verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp), tint = Color.Gray)
                            Spacer(Modifier.width(4.dp))
                            Text("Delete", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                } else {
                    Text("Sold", color = ecoGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}
