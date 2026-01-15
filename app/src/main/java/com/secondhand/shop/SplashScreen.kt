package com.secondhand.shop

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
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit) {

    // This effect runs once when the screen is shown
    LaunchedEffect(Unit) {
        delay(3000) // Wait for 3 seconds
        onNavigateToLogin() // Navigate to the next screen
    }

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
            // 1. App Logo (Replace 'ic_launcher_foreground' with your actual logo name)
            // If you don't have a logo yet, this uses a default Android icon
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_revert),
                contentDescription = "App Logo",
                modifier = Modifier.size(100.dp),
                tint = Color(0xFF4CAF50) // A nice green for second-hand/eco vibe
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2. App Name
            Text(
                text = "Second-Hand Shop",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tagline
            Text(
                text = "Buy & Sell with Ease",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 3. Loading Indicator
            CircularProgressIndicator(
                color = Color(0xFF4CAF50),
                strokeWidth = 3.dp,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}