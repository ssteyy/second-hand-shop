package com.secondhand.shop.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.secondhand.shop.model.Chat
import com.secondhand.shop.model.Notification

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    onChatClick: (String) -> Unit // Added explicitly here
) {
    val ecoGreen = Color(0xFF4CAF50)
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val db = FirebaseFirestore.getInstance()

    var unreadChats by remember { mutableStateOf<List<Chat>>(emptyList()) }
    var systemNotifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(currentUserId) {
        if (currentUserId.isEmpty()) return@LaunchedEffect

        db.collection("chats")
            .whereArrayContains("members", currentUserId)
            .addSnapshotListener { snapshot, _ ->
                unreadChats = snapshot?.toObjects(Chat::class.java)?.filter {
                    it.unreadCountForUser(currentUserId) > 0
                } ?: emptyList()
            }

        db.collection("notifications")
            .whereEqualTo("userId", currentUserId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                systemNotifications = snapshot?.toObjects(Notification::class.java) ?: emptyList()
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ecoGreen)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF8F8F8))) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = ecoGreen)
            } else if (unreadChats.isEmpty() && systemNotifications.isEmpty()) {
                Text("No new updates", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    if (unreadChats.isNotEmpty()) {
                        item { SectionHeader("New Messages") }
                        items(unreadChats) { chat ->
                            ChatNotificationItem(
                                chat = chat,
                                currentUserId = currentUserId,
                                onClick = { onChatClick(chat.id) } // Now this parameter exists!
                            )
                        }
                    }

                    if (systemNotifications.isNotEmpty()) {
                        item { SectionHeader("Activity") }
                        items(systemNotifications) { note ->
                            NotificationItem(
                                title = note.title,
                                body = note.body,
                                time = note.formattedTime(),
                                icon = getIconForType(note.type),
                                iconColor = getIconColorForType(note.type)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatNotificationItem(
    chat: Chat,
    currentUserId: String,
    onClick: () -> Unit
) {
    var displayBody by remember { mutableStateOf(chat.lastMessage ?: "New Message") }
    var senderName by remember { mutableStateOf("Loading...") }

    LaunchedEffect(chat.id) {
        val db = FirebaseFirestore.getInstance()
        db.collection("chats").document(chat.id)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, _ ->
                val messageDoc = snapshot?.documents?.firstOrNull()
                if (messageDoc != null) {
                    displayBody = messageDoc.getString("text") ?: ""
                    val senderId = messageDoc.getString("senderId") ?: ""

                    if (senderId.isNotEmpty()) {
                        db.collection("users").document(senderId).get()
                            .addOnSuccessListener { userDoc ->
                                // Using fullName from your User model
                                senderName = userDoc.getString("fullName") ?: "Unknown User"
                            }
                    }
                }
            }
    }

    NotificationItem(
        title = senderName,
        body = displayBody,
        time = chat.lastMessageTimeFormatted(),
        icon = Icons.AutoMirrored.Filled.Chat,
        iconColor = Color(0xFF4CAF50),
        onClick = onClick
    )
}

@Composable
fun NotificationItem(
    title: String,
    body: String,
    time: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 1.dp)
            .clickable { onClick() },
        color = Color.White
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Surface(shape = CircleShape, color = iconColor.copy(alpha = 0.1f), modifier = Modifier.size(45.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = iconColor, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(time, fontSize = 11.sp, color = Color.Gray)
                }
                Text(body, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(text, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(16.dp))
}

fun getIconForType(type: String): ImageVector = when (type) {
    "like" -> Icons.Default.Favorite
    "order" -> Icons.Default.ShoppingCart
    else -> Icons.Default.Notifications
}

fun getIconColorForType(type: String): Color = when (type) {
    "like" -> Color(0xFFE91E63)
    "order" -> Color(0xFF2196F3)
    else -> Color(0xFFFF9800)
}