package com.secondhand.shop.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.secondhand.shop.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(Unit) {
        delay(3000) // splash delay

        if (auth.currentUser != null) {
            // User already logged in
            onNavigateToMain()
        } else {
            // User not logged in
            onNavigateToLogin()
        }
    }

    // ===== UI (UNCHANGED) =====
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.mipmap.second_hand_shop_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(200.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Second-Hand Shop",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Buy & Sell with Ease",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            CircularProgressIndicator(
                color = Color(0xFF4CAF50),
                strokeWidth = 3.dp,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}
