package com.secondhand.shop.screens.products

import android.app.Activity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secondhand.shop.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageListingsScreen(onBack: () -> Unit, onEditProduct: () -> Unit) {
    val ecoGreen = Color(0xFF4CAF50)
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Active", "Sold")
    val view = LocalView.current

    // This block colors the system status bar (red circle area)
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = ecoGreen.toArgb()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                // This tells the AppBar to "ignore" the top safe padding and draw into it
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "My Listings",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F8F8))
        ) {
            // --- Custom Tab Row ---
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = ecoGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ecoGreen
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
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) ecoGreen else Color.Gray
                            )
                        }
                    )
                }
            }

            // --- Listings List ---
            if (selectedTab == 0) {
                ActiveListingsContent(ecoGreen, onEditProduct)
            } else {
                SoldListingsContent()
            }
        }
    }
}

@Composable
fun ActiveListingsContent(ecoGreen: Color, onEdit: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Placeholder data
        items(5) {
            ListingItemCard(
                title = "Modern Wooden Chair",
                price = "$45.00",
                status = "Active",
                ecoGreen = ecoGreen,
                onEdit = onEdit
            )
        }
    }
}

@Composable
fun SoldListingsContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("No sold items yet", color = Color.Gray)
            Text("Keep sharing to help the planet!", fontSize = 12.sp, color = Color.LightGray)
        }
    }
}

@Composable
fun ListingItemCard(
    title: String,
    price: String,
    status: String,
    ecoGreen: Color,
    onEdit: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier
                    .size(85.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Info Column
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = ecoGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = status,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        color = ecoGreen,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = price,
                    color = ecoGreen,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp
                )

                // Action Buttons Row
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Edit Button
                    Row(
                        modifier = Modifier.clickable { onEdit() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Text(" Edit", fontSize = 12.sp, color = Color.Gray)
                    }

                    // Delete Button
                    Row(
                        modifier = Modifier.clickable { /* Delete Logic */ },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Text(" Delete", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            // Quick Menu
            IconButton(onClick = { /* More options */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.LightGray)
            }
        }
    }
}