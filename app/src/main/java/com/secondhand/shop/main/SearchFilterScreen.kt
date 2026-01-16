package com.secondhand.shop.main

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFilterScreen(onBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val ecoGreen = Color(0xFF4CAF50)
    val white = Color.White

    Scaffold(
        topBar = {
            // Surface matches the style of MainScreen's Scaffold/TopBar container
            Surface(
                shadowElevation = 0.dp, // Matches TopAppBar default look
                color = ecoGreen
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                ) {
                    // 1. Exact TopAppBar style from MainScreen
                    TopAppBar(
                        windowInsets = WindowInsets(0.dp), // Removes default padding gaps
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Consistent Title text style from Home
                                Text(
                                    text = "Search & Filter",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = white
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = white
                                )
                            }
                        },
                        actions = {
                            // Reset text button in white to match top bar actions
                            TextButton(onClick = { searchQuery = "" }) {
                                Text("Reset", color = white, fontWeight = FontWeight.SemiBold)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = ecoGreen,
                            titleContentColor = white,
                            actionIconContentColor = white,
                            navigationIconContentColor = white
                        )
                    )

                    // 2. Search Bar Box tucked under the title row within the green area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(white)
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text("Search for items...", color = Color.Gray, fontSize = 14.sp)
                                },
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = ecoGreen
                                ),
                                singleLine = true
                            )
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFAFAFA))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // --- Recent Searches ---
            if (searchQuery.isEmpty()) {
                Text("Recent Searches", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.padding(vertical = 12.dp)) {
                    RecentSearchChip("iPhone 13")
                    Spacer(modifier = Modifier.width(8.dp))
                    RecentSearchChip("IKEA Desk")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- Filter Sections ---
            FilterCard(title = "Category") {
                val categories = listOf("Electronics", "Furniture", "Fashion", "Books", "Sports", "Toys")
                FilterChipGroup(categories, ecoGreen)
            }

            FilterCard(title = "Product Condition") {
                val conditions = listOf("Brand New", "Like New", "Good", "Used", "For Parts")
                FilterChipGroup(conditions, ecoGreen)
            }

            FilterCard(title = "Price Range") {
                var sliderPosition by remember { mutableStateOf(0f..1000f) }
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        PriceLabel("Min", "${sliderPosition.start.toInt()} $", ecoGreen)
                        PriceLabel("Max", "${sliderPosition.endInclusive.toInt()} $", ecoGreen)
                    }
                    RangeSlider(
                        value = sliderPosition,
                        onValueChange = { sliderPosition = it },
                        valueRange = 0f..1000f,
                        colors = SliderDefaults.colors(
                            thumbColor = white,
                            activeTrackColor = ecoGreen,
                            inactiveTrackColor = ecoGreen.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Apply Button ---
            Button(
                onClick = { /* Logic to apply filters */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ecoGreen),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("Show Results", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- Helper Components ---

@Composable
fun FilterCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun PriceLabel(label: String, value: String, color: Color) {
    Column {
        Text(label, fontSize = 11.sp, color = Color.Gray)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = color)
    }
}

@Composable
fun RecentSearchChip(text: String) {
    Surface(
        color = Color(0xFFEFEFEF),
        shape = CircleShape,
        modifier = Modifier.height(32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(Icons.Default.History, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, fontSize = 12.sp, color = Color.DarkGray)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChipGroup(items: List<String>, ecoGreen: Color) {
    var selectedItem by remember { mutableStateOf(items[0]) }
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach { item ->
            FilterChip(
                selected = (selectedItem == item),
                onClick = { selectedItem = item },
                label = { Text(item, fontSize = 13.sp) },
                shape = RoundedCornerShape(10.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ecoGreen.copy(alpha = 0.15f),
                    selectedLabelColor = ecoGreen,
                    containerColor = Color(0xFFF8F8F8),
                    labelColor = Color.Gray
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedItem == item,
                    borderColor = Color.Transparent,
                    selectedBorderColor = ecoGreen
                )
            )
        }
    }
}