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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun UserScreen(
    viewModel: ProfileViewModel,
    onNavigateToEdit: () -> Unit,
    onNavigateToListings: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    val ecoGreen = Color(0xFF4CAF50)
    val context = LocalContext.current

    // collectAsStateWithLifecycle is more efficient for UI performance
    val user by viewModel.user.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {

            // --- Profile Header ---
            // Using a unique key prevents the list from "resetting" or flickering
            item(key = "header") {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(vertical = 24.dp, horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(ecoGreen.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!user?.profileImage.isNullOrEmpty()) {
                                // Corrected AsyncImage with ImageRequest for crossfade
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(user?.profileImage)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Profile Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = ecoGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (user == null) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = ecoGreen,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = user?.fullName ?: "Guest User",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Text(
                                text = user?.email ?: "",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = onNavigateToEdit,
                                colors = ButtonDefaults.buttonColors(containerColor = ecoGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(40.dp),
                                contentPadding = PaddingValues(horizontal = 20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Edit Profile",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // --- Menu Options ---
            item(key = "menu") {
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

                    Spacer(modifier = Modifier.height(24.dp))

                    SectionHeader("Support")

                    ProfileMenuItem(
                        title = "Help Center",
                        icon = Icons.Default.HelpOutline,
                        onClick = {}
                    )

                    ProfileMenuItem(
                        title = "Privacy Policy",
                        icon = Icons.Default.PrivacyTip,
                        onClick = {}
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
        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
    )
}

@Composable
fun ProfileMenuItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
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