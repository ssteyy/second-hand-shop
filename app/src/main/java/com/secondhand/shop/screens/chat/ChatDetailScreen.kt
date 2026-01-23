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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.secondhand.shop.model.Message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatId: String,
    userName: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val ecoGreen = Color(0xFF4CAF50)
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    var messageText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    val listState = rememberLazyListState()

    // --- Reset Unread Count when entering chat ---
    LaunchedEffect(chatId) {
        if (currentUserId != null) {
            db.collection("chats").document(chatId)
                .update("unreadCounts.$currentUserId", 0)
        }
    }

    // --- Load Messages ---
    LaunchedEffect(chatId) {
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                messages = snapshot?.documents?.mapNotNull { it.toObject(Message::class.java) } ?: emptyList()
            }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) Toast.makeText(context, "Photo selected", Toast.LENGTH_SHORT).show()
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
                            if (messageText.isNotBlank() && currentUserId != null) {
                                val textToSend = messageText
                                val ts = Timestamp.now()
                                messageText = ""

                                // 1. Save message to sub-collection
                                val newMessage = Message(
                                    senderId = currentUserId,
                                    text = textToSend,
                                    timestamp = ts
                                )
                                db.collection("chats").document(chatId).collection("messages").add(newMessage)

                                // 2. ✅ Sync the Parent Document!
                                // This makes "hello" appear in Chat List and Notifications
                                db.collection("chats").document(chatId).get().addOnSuccessListener { doc ->
                                    val members = doc.get("members") as? List<String> ?: emptyList()
                                    val recipientId = members.firstOrNull { it != currentUserId }

                                    val updates = mutableMapOf<String, Any>(
                                        "lastMessage" to textToSend,
                                        "lastMessageTime" to ts
                                    )

                                    // Increment unread count for the other person
                                    if (recipientId != null) {
                                        updates["unreadCounts.$recipientId"] = FieldValue.increment(1)
                                    }

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
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(isMe = message.senderId == currentUserId, text = message.text)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(isMe: Boolean, text: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (isMe) Color(0xFF4CAF50) else Color.White,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 0.dp,
                bottomEnd = if (isMe) 0.dp else 16.dp
            ),
            tonalElevation = 1.dp
        ) {
            Text(
                text,
                Modifier.padding(14.dp, 10.dp),
                color = if (isMe) Color.White else Color.Black,
                fontSize = 14.sp
            )
        }
    }
}