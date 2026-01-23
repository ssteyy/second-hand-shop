package com.secondhand.shop.screens.chat

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.secondhand.shop.model.Message
import com.secondhand.shop.utils.CloudinaryUploader // ✅ Using your util
import kotlinx.coroutines.launch
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatId: String,
    userName: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope() // ✅ Needed for suspend functions
    val ecoGreen = Color(0xFF4CAF50)
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val db = FirebaseFirestore.getInstance()

    var messageText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var isUploading by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // --- Reset Unread Count ---
    LaunchedEffect(chatId) {
        if (currentUserId.isNotEmpty()) {
            db.collection("chats").document(chatId).update("unreadCounts.$currentUserId", 0)
        }
    }

    // --- Load Messages ---
    LaunchedEffect(chatId) {
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                messages = snapshot?.documents?.mapNotNull { it.toObject(Message::class.java) } ?: emptyList()
            }
    }

    // ✅ IMAGE PICKER LOGIC USING YOUR UTILS
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                isUploading = true
                val resultUrl = CloudinaryUploader.uploadFile(context, uri)

                if (resultUrl != null) {
                    val ts = Timestamp.now()
                    val newMessage = Message(
                        senderId = currentUserId,
                        text = "[Image]",
                        imageUrl = resultUrl,
                        timestamp = ts
                    )

                    // 1. Add to Firestore
                    db.collection("chats").document(chatId).collection("messages").add(newMessage)

                    // 2. Sync Parent Doc
                    val updates = mutableMapOf<String, Any>(
                        "lastMessage" to "Sent an image",
                        "lastMessageTime" to ts
                    )

                    db.collection("chats").document(chatId).get().addOnSuccessListener { doc ->
                        val members = doc.get("members") as? List<String> ?: emptyList()
                        val recipientId = members.firstOrNull { it != currentUserId }
                        if (recipientId != null) {
                            updates["unreadCounts.$recipientId"] = FieldValue.increment(1)
                        }
                        db.collection("chats").document(chatId).update(updates)
                    }
                } else {
                    Toast.makeText(context, "Cloudinary upload failed", Toast.LENGTH_SHORT).show()
                }
                isUploading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(userName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Online", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ecoGreen, titleContentColor = Color.White)
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp, shadowElevation = 8.dp, color = Color.White) {
                Column {
                    if (isUploading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = ecoGreen)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 8.dp)
                            .navigationBarsPadding()
                            .imePadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                            Icon(Icons.Default.AttachFile, "Attach", tint = Color.Gray)
                        }

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
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        FloatingActionButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    val textToSend = messageText
                                    val ts = Timestamp.now()
                                    messageText = ""

                                    val newMessage = Message(senderId = currentUserId, text = textToSend, timestamp = ts)
                                    db.collection("chats").document(chatId).collection("messages").add(newMessage)

                                    db.collection("chats").document(chatId).get().addOnSuccessListener { doc ->
                                        val members = doc.get("members") as? List<String> ?: emptyList()
                                        val recipientId = members.firstOrNull { it != currentUserId }
                                        val updates = mutableMapOf<String, Any>("lastMessage" to textToSend, "lastMessageTime" to ts)
                                        if (recipientId != null) updates["unreadCounts.$recipientId"] = FieldValue.increment(1)
                                        db.collection("chats").document(chatId).update(updates)
                                    }
                                }
                            },
                            containerColor = ecoGreen,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.size(48.dp),
                            elevation = FloatingActionButtonDefaults.elevation(0.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, "Send", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5)),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
        ) {
            items(messages) { message ->
                ChatBubble(
                    isMe = message.senderId == currentUserId,
                    text = message.text,
                    imageUrl = message.imageUrl
                )
            }
        }
    }
}

@Composable
fun ChatBubble(isMe: Boolean, text: String, imageUrl: String? = null) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (isMe) Color(0xFF4CAF50) else Color.White,
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 0.dp,
                bottomEnd = if (isMe) 0.dp else 16.dp
            ),
            tonalElevation = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                if (!imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Image message",
                        modifier = Modifier
                            .sizeIn(maxWidth = 200.dp, maxHeight = 200.dp)
                            .background(Color.LightGray, RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    if (text.isNotEmpty() && text != "[Image]") Spacer(modifier = Modifier.height(4.dp))
                }
                if (text.isNotEmpty() && text != "[Image]") {
                    Text(text = text, color = if (isMe) Color.White else Color.Black, fontSize = 14.sp)
                }
            }
        }
    }
}