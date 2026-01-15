package com.secondhand.shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // Call your SplashScreen here
                SplashScreen(
                    onNavigateToLogin = {
                        // Navigate to LoginScreen or next Composable
                        println("Navigate to Login!")
                    }
                )
            }
        }
    }
}
