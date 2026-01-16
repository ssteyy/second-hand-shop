package com.secondhand.shop.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserScreen(
    onNavigateToEdit: () -> Unit,
    onNavigateToListings: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    val ecoGreen = Color(0xFF4CAF50)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .height(30.dp)
            .background(Color(0xFFF8F8F8))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // --- Profile Header Card (SHORTER VERSION) ---
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(vertical = 16.dp, horizontal = 24.dp), // Reduced vertical padding
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Smaller Profile Icon
                        Box(
                            modifier = Modifier
                                .size(80.dp) // Reduced from 100.dp
                                .clip(CircleShape)
                                .background(ecoGreen.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = ecoGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp)) // Reduced spacer

                        Text(
                            text = "Sok Nimol",
                            fontSize = 20.sp, // Slightly smaller text
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Text(
                            text = "sok.nimol@email.com",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(12.dp)) // Reduced spacer

                        // Compact Edit Button
                        Button(
                            onClick = onNavigateToEdit,
                            colors = ButtonDefaults.buttonColors(containerColor = ecoGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(38.dp), // Reduced height
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // --- Menu Options ---
            item {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    SectionHeader("Account Settings")

                    ProfileMenuItem(
                        title = "My Listings",
                        icon = Icons.AutoMirrored.Filled.ListAlt,
                        onClick = onNavigateToListings
                    )

                    ProfileMenuItem(
                        title = "Saved Items",
                        icon = Icons.Default.FavoriteBorder,
                        onClick = onNavigateToFavorites
                    )

                    ProfileMenuItem(
                        title = "Settings",
                        icon = Icons.Default.Settings,
                        onClick = onNavigateToSettings
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    SectionHeader("Support")

                    ProfileMenuItem(
                        title = "Help Center",
                        icon = Icons.Default.HelpOutline,
                        onClick = { /* Navigate to Help */ }
                    )

                    ProfileMenuItem(
                        title = "Privacy Policy",
                        icon = Icons.Default.PrivacyTip,
                        onClick = { /* Navigate to Privacy */ }
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )
}

@Composable
fun ProfileMenuItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp) // Tighter list spacing
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp), // Slightly more compact padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = Color.Black
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}