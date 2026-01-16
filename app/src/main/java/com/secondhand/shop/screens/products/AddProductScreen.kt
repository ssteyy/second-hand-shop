package com.secondhand.shop.screens.products

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
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
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.secondhand.shop.R
import com.secondhand.shop.screens.main.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddProductScreen(onBack: () -> Unit, onPostSuccess: () -> Unit) {
    val ecoGreen = Color(0xFF4CAF50)
    val darkEcoGreen = Color(0xFF388E3C)
    val white = Color.White

    // --- Permission & Image State ---
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var capturedImage by remember { mutableStateOf<Any?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // --- Form States ---
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Condition Dropdown States
    var conditionExpanded by remember { mutableStateOf(false) }
    val conditions = listOf("Brand New", "Like New", "Lightly Used", "Well Used", "For Parts")
    var selectedCondition by remember { mutableStateOf(conditions[0]) }

    // Category Dropdown States
    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf("Electronics", "Furniture", "Fashion", "Home Decor", "Books", "Toys", "Other")
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var customCategoryName by remember { mutableStateOf("") }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) capturedImage = uri
        showSheet = false
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) capturedImage = bitmap
        showSheet = false
    }

    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = ecoGreen,
        focusedLabelColor = ecoGreen,
        cursorColor = ecoGreen,
        focusedTrailingIconColor = ecoGreen
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Post New Item",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = white
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = white
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = white,
                    navigationIconContentColor = white
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = ecoGreen, tonalElevation = 8.dp) {
                val items = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Favorites,
                    BottomNavItem.Sell,
                    BottomNavItem.Chat,
                    BottomNavItem.Profile
                )

                items.forEach { item ->
                    val isSelected = item == BottomNavItem.Sell
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 10.sp, color = if (isSelected) white else white.copy(alpha = 0.7f)) },
                        selected = isSelected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = white,
                            unselectedIconColor = white.copy(alpha = 0.7f),
                            indicatorColor = darkEcoGreen
                        ),
                        onClick = { if (!isSelected) onBack() }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Image Upload Section ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier
                        .size(160.dp)
                        .clickable { showSheet = true },
                    color = Color(0xFFF8F8F8),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(2.dp, if(capturedImage != null) ecoGreen else ecoGreen.copy(alpha = 0.2f))
                ) {
                    if (capturedImage != null) {
                        AsyncImage(
                            model = capturedImage,
                            contentDescription = "Product Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.AddAPhoto, null, tint = ecoGreen, modifier = Modifier.size(32.dp))
                            Text("Add Photo", color = ecoGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // --- Title Field ---
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("What are you selling?") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = customTextFieldColors
            )

            // --- Category Selection ---
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        colors = customTextFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                if (selectedCategory == "Other") {
                    OutlinedTextField(
                        value = customCategoryName,
                        onValueChange = { customCategoryName = it },
                        label = { Text("Category Name") },
                        placeholder = { Text("Enter your custom category") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = customTextFieldColors,
                        singleLine = true
                    )
                }
            }

            // --- Price and Condition Row ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price ($)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = customTextFieldColors,
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = conditionExpanded,
                    onExpandedChange = { conditionExpanded = !conditionExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedCondition,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Condition") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = conditionExpanded) },
                        colors = customTextFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = conditionExpanded,
                        onDismissRequest = { conditionExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        conditions.forEach { condition ->
                            DropdownMenuItem(
                                text = { Text(condition) },
                                onClick = {
                                    selectedCondition = condition
                                    conditionExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // --- Description Field ---
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = customTextFieldColors
            )

            // --- Submit Button ---
            Button(
                onClick = onPostSuccess,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ecoGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Post Item Now", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp, start = 20.dp, end = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Select Photo Source", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    ListItem(
                        headlineContent = { Text("Take a Photo") },
                        leadingContent = { Icon(Icons.Default.PhotoCamera, null, tint = ecoGreen) },
                        modifier = Modifier.clickable {
                            if (cameraPermissionState.status.isGranted) cameraLauncher.launch()
                            else cameraPermissionState.launchPermissionRequest()
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Choose from Gallery") },
                        leadingContent = { Icon(Icons.Default.PhotoLibrary, null, tint = ecoGreen) },
                        modifier = Modifier.clickable { galleryLauncher.launch("image/*") }
                    )
                }
            }
        }
    }
}