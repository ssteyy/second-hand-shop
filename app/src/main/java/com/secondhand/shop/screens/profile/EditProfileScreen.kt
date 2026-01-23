package com.secondhand.shop.screens.profile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    profileViewModel: ProfileViewModel,
    onBack: () -> Unit
) {
    val ecoGreen = Color(0xFF4CAF50)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // --- Fetch current user data from ViewModel ---
    val user by profileViewModel.user.collectAsState()

    // --- State ---
    var name by remember { mutableStateOf(user?.fullName ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }
    var bio by remember { mutableStateOf(user?.bio ?: "") }

    // ✅ Add this line to fix the "Unresolved reference" error
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        user?.let {
            name = it.fullName ?: ""
            email = it.email ?: ""
            phone = it.phone ?: ""
            bio = it.bio ?: ""
        }
    }

    // Image States
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

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
        // --- Header ---
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
                    // Show a loader if saving, otherwise show the text button
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp).padding(end = 16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    isSaving = true
                                    val db = Firebase.firestore
                                    val uid = user?.uid ?: return@launch

                                    // 1. Actually upload the image using your Cloudinary utility
                                    val uploadedUrl = uploadProfileImage(context, capturedBitmap, selectedImageUri)

                                    // 2. Determine which URL to keep
                                    val finalImageUrl = uploadedUrl ?: user?.profileImage ?: ""

                                    val updatedUser = mapOf(
                                        "fullName" to name,
                                        "email" to email,
                                        "phone" to phone,
                                        "bio" to bio,
                                        "profileImage" to finalImageUrl
                                    )

                                    db.collection("users")
                                        .document(uid)
                                        .update(updatedUser)
                                        .addOnSuccessListener {
                                            isSaving = false
                                            onBack()
                                        }
                                        .addOnFailureListener { e ->
                                            isSaving = false
                                        }
                                }
                            }
                        ) {
                            Text(
                                text = "Save",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ecoGreen)
            )
        }

        // --- Content ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // --- Profile Image ---
            Box {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        capturedBitmap != null -> Image(
                            bitmap = capturedBitmap!!.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        selectedImageUri != null -> AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        !user?.profileImage.isNullOrEmpty() -> AsyncImage(
                            model = user!!.profileImage,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        else -> Text(
                            text = name.take(2).uppercase(),
                            fontSize = 28.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
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

            // --- Form Fields ---
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

    // --- Bottom Sheet: Pick Image ---
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
                        cameraLauncher.launch(null)
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

// --- Helpers ---
@Composable
fun EditFieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier
            .fillMaxWidth()
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

// --- Updated Image Upload Helper ---
suspend fun uploadProfileImage(context: Context, bitmap: Bitmap?, uri: Uri?): String? {
    return when {
        bitmap != null -> {
            com.secondhand.shop.utils.CloudinaryUploader.uploadBitmap(bitmap)
        }
        uri != null -> {
            com.secondhand.shop.utils.CloudinaryUploader.uploadFile(context, uri)
        }
        else -> null
    }
}