package com.secondhand.shop.screens.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secondhand.shop.R

@Composable
fun ChatListScreen(onChatClick: (String) -> Unit) {
    // The TopAppBar should now be placed in your MainScreen.kt 
    // inside the Scaffold's topBar = { ... } block.

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        items(10) { index ->
            ChatItem(
                name = if (index % 2 == 0) "John Doe" else "Sarah Green",
                lastMsg = "Is the wooden chair still available for purchase?",
                time = "12:45 PM",
                unreadCount = if (index < 2) 2 else 0,
                onClick = { onChatClick(if (index % 2 == 0) "John Doe" else "Sarah Green") }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun ChatItem(name: String, lastMsg: String, time: String, unreadCount: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with unread indicator
        Box {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
            )
            if (unreadCount > 0) {
                Surface(
                    color = Color(0xFF4CAF50),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(14.dp)
                        .border(2.dp, Color.White, CircleShape)
                ) {}
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Message Content
        Column(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = time,
                    color = if (unreadCount > 0) Color(0xFF4CAF50) else Color.Gray,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = lastMsg,
                color = if (unreadCount > 0) Color.Black else Color.Gray,
                fontWeight = if (unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp
            )
        }
    }
}