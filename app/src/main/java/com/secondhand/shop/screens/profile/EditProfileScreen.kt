package com.secondhand.shop.screens.profile

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(onBack: () -> Unit) {
    // --- State Management ---
    var name by remember { mutableStateOf("Sok Nimol") }
    var email by remember { mutableStateOf("sok.nimol@email.com") }
    var phone by remember { mutableStateOf("+855 12 345 678") }
    var bio by remember { mutableStateOf("Eco-friendly enthusiast and photography lover.") }

    // Image States
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showSheet by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val ecoGreen = Color(0xFF4CAF50)

    // --- Launchers ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            capturedBitmap = null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            selectedImageUri = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- 1. Top Action Row with EcoGreen Background ---
        Surface(color = ecoGreen) {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
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
                actions = {
                    TextButton(onClick = onBack) {
                        Text(
                            text = "Save",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ecoGreen
                )
            )
        }

        // --- Content Section (Scrollable) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // --- 2. Profile Photo Edit ---
            Box {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        capturedBitmap != null -> {
                            Image(
                                bitmap = capturedBitmap!!.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        selectedImageUri != null -> {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Text("SN", fontSize = 28.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                IconButton(
                    onClick = { showSheet = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(ecoGreen, CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- 3. Form Fields (Labels Aligned Left) ---
            Column(modifier = Modifier.fillMaxWidth()) {
                EditFieldLabel("Full Name")
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = editFieldColors(ecoGreen)
                )

                Spacer(modifier = Modifier.height(16.dp))

                EditFieldLabel("Email Address")
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = editFieldColors(ecoGreen)
                )

                Spacer(modifier = Modifier.height(16.dp))

                EditFieldLabel("Phone Number")
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = editFieldColors(ecoGreen)
                )

                Spacer(modifier = Modifier.height(16.dp))

                EditFieldLabel("Bio")
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4,
                    colors = editFieldColors(ecoGreen)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // --- Image Picker Bottom Sheet ---
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            ) {
                Text(
                    "Profile Photo",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                ListItem(
                    headlineContent = { Text("Take Photo") },
                    leadingContent = { Icon(Icons.Default.CameraAlt, null, tint = ecoGreen) },
                    modifier = Modifier.clickable {
                        showSheet = false
                        cameraLauncher.launch()
                    }
                )
                ListItem(
                    headlineContent = { Text("Choose from Gallery") },
                    leadingContent = { Icon(Icons.Default.PhotoLibrary, null, tint = ecoGreen) },
                    modifier = Modifier.clickable {
                        showSheet = false
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun EditFieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier
            .fillMaxWidth() // Added to ensure it respects parent width
            .padding(start = 4.dp, bottom = 6.dp)
    )
}

@Composable
fun editFieldColors(ecoGreen: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ecoGreen,
    unfocusedBorderColor = Color(0xFFEFEFEF),
    focusedLabelColor = ecoGreen,
    cursorColor = ecoGreen,
    unfocusedContainerColor = Color(0xFFFAFAFA),
    focusedContainerColor = Color.White
)