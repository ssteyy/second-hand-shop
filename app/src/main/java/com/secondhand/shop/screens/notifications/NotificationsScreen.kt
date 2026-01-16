package com.secondhand.shop.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotificationsScreen(onBack: () -> Boolean) {
    // The TopAppBar should now be placed in your MainScreen.kt
    // inside the Scaffold's topBar = { ... } block to ensure consistency.

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        // Sample static data
        item {
            NotificationItem(
                title = "Order Placed",
                body = "Your order for 'Vintage Lamp' has been received.",
                time = "2m ago",
                icon = Icons.Default.ShoppingCart,
                iconColor = Color(0xFF2196F3)
            )
        }
        item {
            NotificationItem(
                title = "New Like",
                body = "Someone liked your 'Wooden Chair' listing.",
                time = "1h ago",
                icon = Icons.Default.Favorite,
                iconColor = Color(0xFFE91E63)
            )
        }
        item {
            NotificationItem(
                title = "System Update",
                body = "New features are available! Check them out.",
                time = "Yesterday",
                icon = Icons.Default.Notifications,
                iconColor = Color(0xFFFF9800)
            )
        }

        // Add more items to fill the screen
        items(5) { index ->
            NotificationItem(
                title = "Promotion",
                body = "Get 10% off on your next purchase in Electronics!",
                time = "${index + 2} days ago",
                icon = Icons.Default.Notifications,
                iconColor = Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
fun NotificationItem(
    title: String,
    body: String,
    time: String,
    icon: ImageVector,
    iconColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 1.dp), // Creates a thin divider effect
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Rounded Icon Background
            Surface(
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                    Text(
                        text = time,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = body,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}