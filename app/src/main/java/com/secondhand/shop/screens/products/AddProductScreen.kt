package com.secondhand.shop.screens.products

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.secondhand.shop.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    // --- Sold Status State ---
    var isSold by remember { mutableStateOf(false) }

    // --- UI States ---
    var isUploading by remember { mutableStateOf(false) }
    var isLoadingProduct by remember { mutableStateOf(isEditMode) }
    var showSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val scrollState = rememberScrollState()

    val conditions = listOf("Brand New", "Like New", "Lightly Used", "Well Used", "For Parts")
    val categories = listOf("Electronics", "Furniture", "Fashion", "Home Decor", "Books", "Toys", "Other")

    // Load Data if in Edit Mode
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
                        isSold = it.sold // Load the status
                        if (categories.contains(it.category)) {
                            selectedCategory = it.category
                        } else {
                            selectedCategory = "Other"
                            customCategoryName = it.category
                        }
                    }
                    isLoadingProduct = false
                },
                onError = {
                    isLoadingProduct = false
                    Toast.makeText(context, "Error loading product", Toast.LENGTH_SHORT).show()
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
                        text = if (isEditMode) "Edit Listing" else "Post New Item",
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
        }
    ) { padding ->
        if (isLoadingProduct) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ecoGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .imePadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // --- STATUS TOGGLE: Only visible when EDITING ---
                if (isEditMode) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSold) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (isSold) Color.Red.copy(0.3f) else ecoGreen.copy(0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isSold) "Status: SOLD" else "Status: ACTIVE",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSold) Color.Red else darkEcoGreen
                                )
                                Text(
                                    text = if (isSold) "Hidden from search" else "Visible to buyers",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = isSold,
                                onCheckedChange = { isSold = it },
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = Color.Red,
                                    uncheckedTrackColor = ecoGreen
                                )
                            )
                        }
                    }
                }

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

                // Inputs
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Product Title") }, modifier = Modifier.fillMaxWidth(), colors = customTextFieldColors, shape = RoundedCornerShape(12.dp))

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

                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(150.dp), colors = customTextFieldColors, shape = RoundedCornerShape(12.dp))

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (title.isBlank() || price.isBlank() || (capturedImage == null && existingImageUrl == null)) {
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
                                    sold = isSold, // If adding new, isSold is false by default
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
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ecoGreen),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isUploading
                ) {
                    if (isUploading) CircularProgressIndicator(color = white, modifier = Modifier.size(24.dp))
                    else Text(if (isEditMode) "Update Product" else "Post Item Now", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Modal Sheet for Image Selection
    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }, sheetState = sheetState) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp, start = 16.dp, end = 16.dp)) {
                Text("Select Image", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 16.dp))
                ListItem(
                    headlineContent = { Text("Take Photo") },
                    leadingContent = { Icon(Icons.Default.PhotoCamera, null, tint = ecoGreen) },
                    modifier = Modifier.clickable {
                        if (cameraPermissionState.status.isGranted) cameraLauncher.launch(null)
                        else cameraPermissionState.launchPermissionRequest()
                    }
                )
                ListItem(
                    headlineContent = { Text("Gallery") },
                    leadingContent = { Icon(Icons.Default.PhotoLibrary, null, tint = ecoGreen) },
                    modifier = Modifier.clickable { galleryLauncher.launch("image/*") }
                )
            }
        }
    }
}