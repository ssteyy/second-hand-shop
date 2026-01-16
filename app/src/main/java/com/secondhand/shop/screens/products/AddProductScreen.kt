package com.secondhand.shop.screens.products

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddProductScreen(onBack: () -> Unit, onPostSuccess: () -> Unit) {
    val ecoGreen = Color(0xFF4CAF50)
    val white = Color.White
    val context = LocalContext.current

    // --- Permission & Image State ---
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var capturedImage by remember { mutableStateOf<Any?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // --- Form States ---
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Dropdown States
    var expanded by remember { mutableStateOf(false) }
    val conditions = listOf("Brand New", "Like New", "Lightly Used", "Well Used", "For Parts")
    var selectedCondition by remember { mutableStateOf(conditions[0]) }

    // 1. Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) capturedImage = uri
        showSheet = false
    }

    // 2. Camera Launcher
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
                        // Changed to ArrowBack and color to White
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = white
                        )
                    }
                },
                // Updated TopAppBar colors to ecoGreen
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ecoGreen
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- 1. IMAGE UPLOAD SECTION ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Photos", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier
                        .size(180.dp)
                        .clickable { showSheet = true },
                    color = Color(0xFFF8F8F8),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(2.dp, if(capturedImage != null) ecoGreen else ecoGreen.copy(alpha = 0.3f))
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
                            Icon(Icons.Default.AddAPhoto, null, tint = ecoGreen, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Add Photo", color = ecoGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // --- 2. FORM FIELDS ---
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("What are you selling?") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = customTextFieldColors
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price ($)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = customTextFieldColors
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedCondition,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Condition") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = customTextFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        conditions.forEach { condition ->
                            DropdownMenuItem(
                                text = { Text(condition) },
                                onClick = {
                                    selectedCondition = condition
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                shape = RoundedCornerShape(12.dp),
                colors = customTextFieldColors
            )

            Button(
                onClick = onPostSuccess,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ecoGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Post Item Now", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        // --- BOTTOM SHEET FOR SELECTION ---
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp, start = 20.dp, end = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Select Photo Source", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    ListItem(
                        headlineContent = { Text("Take a Photo") },
                        leadingContent = { Icon(Icons.Default.PhotoCamera, null, tint = ecoGreen) },
                        modifier = Modifier.clickable {
                            if (cameraPermissionState.status.isGranted) {
                                cameraLauncher.launch()
                            } else {
                                cameraPermissionState.launchPermissionRequest()
                            }
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