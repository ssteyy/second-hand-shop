package com.secondhand.shop.screens.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.secondhand.shop.model.Chat

@Composable
fun ChatListScreen(onChatClick: (chatId: String, userName: String) -> Unit) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val db = FirebaseFirestore.getInstance()

    var chats by remember { mutableStateOf<List<Chat>>(emptyList()) }
    // Note: We use the Chat model's helper functions now instead of a manual user map if possible,
    // but I've kept your logic for profile fetching to ensure compatibility.
    var usersMap by remember { mutableStateOf<Map<String, Pair<String, String>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    DisposableEffect(currentUserId) {
        if (currentUserId.isEmpty()) {
            isLoading = false
            return@DisposableEffect onDispose {}
        }

        // listens to the parent chats collection
        val query = db.collection("chats")
            .whereArrayContains("members", currentUserId)
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)

        val registration: ListenerRegistration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                errorMessage = error.message
                isLoading = false
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val fetchedChats = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Chat::class.java)?.copy(id = doc.id)
                }
                chats = fetchedChats

                fetchedChats.forEach { chat ->
                    val otherUserId = chat.members.firstOrNull { it != currentUserId }
                    if (otherUserId != null && !usersMap.containsKey(otherUserId)) {
                        db.collection("users").document(otherUserId).get()
                            .addOnSuccessListener { userDoc ->
                                val name = userDoc.getString("fullName") ?: "User"
                                val profile = userDoc.getString("profileImage") ?: ""
                                usersMap = usersMap + (otherUserId to Pair(name, profile))
                            }
                    }
                }
            }
            isLoading = false
        }
        onDispose { registration.remove() }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F8F8))) {
        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF4CAF50))
            errorMessage != null -> ErrorView(modifier = Modifier.align(Alignment.Center))
            chats.isEmpty() -> Text("No conversations yet", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(chats) { chat ->
                    val otherUserId = chat.members.firstOrNull { it != currentUserId } ?: ""
                    val userInfo = usersMap[otherUserId]

                    // Priority: Use User Collection data, fallback to Chat model data
                    val otherUserName = userInfo?.first ?: chat.getOtherUserName(currentUserId)
                    val otherUserImg = userInfo?.second ?: chat.getOtherUserProfileImage(currentUserId)

                    ChatItem(
                        name = otherUserName,
                        imageUrl = otherUserImg,
                        // ✅ This 'lastMessage' field is updated by the onSendMessage function below
                        lastMsg = chat.lastMessage ?: "No messages yet",
                        time = chat.lastMessageTimeFormatted(),
                        unreadCount = chat.unreadCountForUser(currentUserId),
                        onClick = { onChatClick(chat.id, otherUserName) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatItem(
    name: String,
    imageUrl: String?,
    lastMsg: String,
    time: String,
    unreadCount: Int,
    onClick: () -> Unit
) {
    val ecoGreen = Color(0xFF4CAF50)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            AsyncImage(
                model = imageUrl.takeIf { !it.isNullOrEmpty() } ?: "https://via.placeholder.com/150",
                contentDescription = null,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
            )
            if (unreadCount > 0) {
                Surface(
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.TopEnd),
                    shape = CircleShape,
                    color = ecoGreen,
                    border = BorderStroke(2.dp, Color.White)
                ) {}
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = time,
                    fontSize = 11.sp,
                    color = if (unreadCount > 0) ecoGreen else Color.Gray
                )
            }
            Text(
                text = lastMsg,
                fontSize = 14.sp,
                color = if (unreadCount > 0) Color.Black else Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ErrorView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.WarningAmber,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Something went wrong", fontWeight = FontWeight.Bold)
    }
}