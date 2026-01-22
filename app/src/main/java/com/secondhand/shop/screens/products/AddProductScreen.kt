package com.secondhand.shop.screens.products

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseAuth
import com.secondhand.shop.model.Product
import com.secondhand.shop.utils.CloudinaryUploader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.secondhand.shop.repository.ProductRepository
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddProductScreen(
    productId: String? = null,
    onBack: () -> Unit,
    onPostSuccess: () -> Unit
) {
    val context = LocalContext.current
    val ecoGreen = Color(0xFF4CAF50)
    val darkEcoGreen = Color(0xFF388E3C)
    val white = Color.White

    val isEditMode = productId != null

    // --- Form States ---
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCondition by remember { mutableStateOf("Brand New") }
    var selectedCategory by remember { mutableStateOf("Electronics") }
    var customCategoryName by remember { mutableStateOf("") }
    var capturedImage by remember { mutableStateOf<Any?>(null) }
    var existingImageUrl by remember { mutableStateOf<String?>(null) }

    // --- UI States ---
    var isUploading by remember { mutableStateOf(false) }
    var isLoadingProduct by remember { mutableStateOf(isEditMode) }
    var showSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    // Scroll state for the body
    val scrollState = rememberScrollState()

    val conditions = listOf("Brand New", "Like New", "Lightly Used", "Well Used", "For Parts")
    val categories = listOf("Electronics", "Furniture", "Fashion", "Home Decor", "Books", "Toys", "Others")

    LaunchedEffect(productId) {
        if (isEditMode && productId != null) {
            ProductRepository.getProductById(
                productId = productId,
                onSuccess = { product ->
                    product?.let {
                        title = it.title
                        price = it.price.toString()
                        description = it.description
                        selectedCondition = it.condition
                        existingImageUrl = it.imageUrl
                        capturedImage = it.imageUrl
                        if (categories.contains(it.category)) {
                            selectedCategory = it.category
                        } else {
                            selectedCategory = "Others"
                            customCategoryName = it.category
                        }
                    }
                    isLoadingProduct = false
                },
                onError = {
                    isLoadingProduct = false
                    Toast.makeText(context, "Error loading product info", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) { capturedImage = uri; existingImageUrl = null }
        showSheet = false
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) { capturedImage = bitmap; existingImageUrl = null }
        showSheet = false
    }

    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = ecoGreen,
        focusedLabelColor = ecoGreen,
        cursorColor = ecoGreen
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Product" else "Post New Item",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = white
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = white)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ecoGreen)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = ecoGreen) {
                val items = listOf(
                    Triple("Home", Icons.Default.Home, "home_content"),
                    Triple("Saved", Icons.Default.Favorite, "favorites"),
                    Triple("Sell", Icons.Default.AddCircle, "sell"),
                    Triple("Chat", Icons.AutoMirrored.Filled.Chat, "chat"),
                    Triple("Profile", Icons.Default.Person, "profile_content")
                )
                items.forEach { (label, icon, route) ->
                    val isSelected = route == "sell"
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, fontSize = 10.sp, color = Color.White) },
                        selected = isSelected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = white,
                            unselectedIconColor = white.copy(alpha = 0.7f),
                            indicatorColor = darkEcoGreen
                        ),
                        onClick = { if (route != "sell") onBack() }
                    )
                }
            }
        }
    ) { padding ->
        if (isLoadingProduct) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ecoGreen)
            }
        } else {
            // Main Scrollable Container
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding) // Respects Top/Bottom Bars
                    .verticalScroll(scrollState) // Enables Scrolling
                    .imePadding() // Moves content up when keyboard appears
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Image Picker
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier.size(160.dp).clickable { showSheet = true },
                        color = Color(0xFFF8F8F8),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(2.dp, ecoGreen.copy(alpha = 0.3f))
                    ) {
                        if (capturedImage != null) {
                            AsyncImage(model = capturedImage, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Icon(Icons.Default.AddAPhoto, null, tint = ecoGreen, modifier = Modifier.size(40.dp))
                                Text("Add Photo", color = ecoGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Title
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Product Title") }, modifier = Modifier.fillMaxWidth(), colors = customTextFieldColors, shape = RoundedCornerShape(12.dp))

                // Category
                var categoryExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = !categoryExpanded }) {
                    OutlinedTextField(value = selectedCategory, onValueChange = {}, readOnly = true, label = { Text("Category") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) }, modifier = Modifier.menuAnchor().fillMaxWidth(), colors = customTextFieldColors, shape = RoundedCornerShape(12.dp))
                    ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                        categories.forEach { cat -> DropdownMenuItem(text = { Text(cat) }, onClick = { selectedCategory = cat; categoryExpanded = false }) }
                    }
                }

                if (selectedCategory == "Other") {
                    OutlinedTextField(value = customCategoryName, onValueChange = { customCategoryName = it }, label = { Text("Custom Category Name") }, modifier = Modifier.fillMaxWidth(), colors = customTextFieldColors, shape = RoundedCornerShape(12.dp))
                }

                // Price and Condition Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price ($)") }, modifier = Modifier.weight(1f), colors = customTextFieldColors, shape = RoundedCornerShape(12.dp))

                    var conditionExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = conditionExpanded, onExpandedChange = { conditionExpanded = !conditionExpanded }, modifier = Modifier.weight(1f)) {
                        OutlinedTextField(value = selectedCondition, onValueChange = {}, readOnly = true, label = { Text("Condition") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = conditionExpanded) }, modifier = Modifier.menuAnchor(), colors = customTextFieldColors, shape = RoundedCornerShape(12.dp))
                        ExposedDropdownMenu(expanded = conditionExpanded, onDismissRequest = { conditionExpanded = false }) {
                            conditions.forEach { cond -> DropdownMenuItem(text = { Text(cond) }, onClick = { selectedCondition = cond; conditionExpanded = false }) }
                        }
                    }
                }

                // Description
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(150.dp), colors = customTextFieldColors, shape = RoundedCornerShape(12.dp))

                Spacer(modifier = Modifier.height(8.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (title.isBlank() || price.isBlank() || capturedImage == null) {
                            Toast.makeText(context, "Please complete all fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isUploading = true
                        scope.launch {
                            val finalImageUrl = if (existingImageUrl != null) existingImageUrl else {
                                withContext(Dispatchers.IO) {
                                    when (capturedImage) {
                                        is Uri -> CloudinaryUploader.uploadFile(context, capturedImage as Uri)
                                        is Bitmap -> CloudinaryUploader.uploadBitmap(capturedImage as Bitmap)
                                        else -> null
                                    }
                                }
                            }

                            if (finalImageUrl != null) {
                                val currentUser = FirebaseAuth.getInstance().currentUser
                                val product = Product(
                                    id = productId ?: "",
                                    title = title,
                                    price = price.toDoubleOrNull() ?: 0.0,
                                    description = description,
                                    category = if (selectedCategory == "Other") customCategoryName else selectedCategory,
                                    condition = selectedCondition,
                                    imageUrl = finalImageUrl,
                                    sellerId = currentUser?.uid ?: "anonymous",
                                    createdAt = System.currentTimeMillis()
                                )

                                if (isEditMode) {
                                    ProductRepository.updateProduct(product, { isUploading = false; onPostSuccess() }, { isUploading = false; Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show() })
                                } else {
                                    ProductRepository.saveProduct(product, { isUploading = false; onPostSuccess() }, { isUploading = false; Toast.makeText(context, "Post failed", Toast.LENGTH_SHORT).show() })
                                }
                            } else {
                                isUploading = false
                                Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ecoGreen),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isUploading
                ) {
                    if (isUploading) CircularProgressIndicator(color = white, modifier = Modifier.size(24.dp))
                    else Text(if (isEditMode) "Edit Product" else "Post Item Now", fontWeight = FontWeight.Bold)
                }

                // Extra spacer to ensure the button isn't hugged by the bottom bar
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal Bottom Sheet remains the same...
    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }, sheetState = sheetState) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp, start = 16.dp, end = 16.dp)) {
                Text("Product Photo", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 16.dp))
                ListItem(
                    headlineContent = { Text("Take New Photo") },
                    leadingContent = { Icon(Icons.Default.PhotoCamera, null, tint = ecoGreen) },
                    modifier = Modifier.clickable {
                        if (cameraPermissionState.status.isGranted) cameraLauncher.launch(null)
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