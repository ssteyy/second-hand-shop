package com.secondhand.shop.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
    val ecoGreen = Color(0xFF4CAF50)
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Privacy Policy",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ecoGreen)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            // Header Icon
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PrivacyTip,
                    contentDescription = null,
                    tint = ecoGreen.copy(alpha = 0.2f),
                    modifier = Modifier.size(80.dp)
                )
            }

            Text(
                text = "Last Updated: January 2026",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            PrivacySection(
                title = "1. Information We Collect",
                content = "When you register for Second Hand Shop, we collect your name, email address, and profile picture. When you post a product, we store the product details, images, and your location to help local buyers find your items."
            )

            PrivacySection(
                title = "2. How We Use Your Information",
                content = "Your information is used to personalize your experience, facilitate communication between buyers and sellers via chat, and improve our marketplace services. We do not sell your personal data to third parties."
            )

            PrivacySection(
                title = "3. Data Storage",
                content = "All user and product data is securely stored using Google Firebase. We implement industry-standard security measures to protect your unauthorized access or disclosure of your information."
            )

            PrivacySection(
                title = "4. Public Visibility",
                content = "Please be aware that your 'Full Name' and any products you list as 'Active' are visible to other users of the app. Your email address and password remain private."
            )

            PrivacySection(
                title = "5. Your Rights",
                content = "You have the right to edit your profile information at any time. If you wish to delete your account and all associated data, please contact our support team."
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "By using this app, you agree to the terms outlined in this Privacy Policy.",
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PrivacySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            fontSize = 14.sp,
            color = Color.DarkGray,
            lineHeight = 22.sp
        )
    }
}