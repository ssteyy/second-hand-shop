package com.secondhand.shop.screens.chat

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(userName: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val ecoGreen = Color(0xFF4CAF50)
    val sheetState = rememberModalBottomSheetState()

    // --- State Management ---
    var messageText by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }

    // --- Launchers for Media ---
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) Toast.makeText(context, "Photo selected", Toast.LENGTH_SHORT).show()
        showSheet = false
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) Toast.makeText(context, "Photo captured", Toast.LENGTH_SHORT).show()
        showSheet = false
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) isRecording = true else Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
    }

    // --- Bottom Sheet for Attachments ---
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp, top = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AttachmentOption(Icons.Default.Photo, "Gallery", Color(0xFF2196F3)) {
                    galleryLauncher.launch("image/*")
                }
                AttachmentOption(Icons.Default.CameraAlt, "Camera", Color(0xFF4CAF50)) {
                    cameraLauncher.launch(null)
                }
                AttachmentOption(Icons.Default.Videocam, "Video", Color(0xFFFF5722)) {
                    // Launch video capture or video picker logic here
                    Toast.makeText(context, "Video mode active", Toast.LENGTH_SHORT).show()
                    showSheet = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Column {
                        Text(userName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Online", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ecoGreen, titleContentColor = Color.White)
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp, shadowElevation = 8.dp, color = Color.White) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp)
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showSheet = true }) {
                        Icon(Icons.Default.AttachFile, "Attach", tint = Color.Gray)
                    }

                    if (isRecording) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .background(Color(0xFFFFEBEE), RoundedCornerShape(24.dp)),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text("Recording audio...", color = Color.Red, modifier = Modifier.padding(start = 16.dp), fontSize = 14.sp)
                        }
                    } else {
                        TextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = { Text("Type a message...", fontSize = 14.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF2F2F2),
                                unfocusedContainerColor = Color(0xFFF2F2F2),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    FloatingActionButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                messageText = ""
                            } else if (isRecording) {
                                isRecording = false
                            } else {
                                val permission = Manifest.permission.RECORD_AUDIO
                                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                                    isRecording = true
                                } else {
                                    permissionLauncher.launch(permission)
                                }
                            }
                        },
                        containerColor = if (isRecording) Color.Red else ecoGreen,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.size(48.dp),
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(
                            imageVector = when {
                                messageText.isNotBlank() -> Icons.AutoMirrored.Filled.Send
                                isRecording -> Icons.Default.Stop
                                else -> Icons.Default.Mic
                            },
                            contentDescription = "Action",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5))) {
            Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shadowElevation = 1.dp) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(45.dp).background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp)))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Modern Wooden Chair", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("$45.00", color = ecoGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(6) { index ->
                    ChatBubble(isMe = index % 2 != 0, text = if (index % 2 != 0) "Is this still available?" else "Yes, it is!")
                }
            }
        }
    }
}

@Composable
fun AttachmentOption(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(color.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(30.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ChatBubble(isMe: Boolean, text: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart) {
        Surface(
            color = if (isMe) Color(0xFF4CAF50) else Color.White,
            shape = RoundedCornerShape(16.dp, 16.dp, if (isMe) 16.dp else 0.dp, if (isMe) 0.dp else 16.dp)
        ) {
            Text(text, Modifier.padding(14.dp, 10.dp), color = if (isMe) Color.White else Color.Black, fontSize = 14.sp)
        }
    }
}