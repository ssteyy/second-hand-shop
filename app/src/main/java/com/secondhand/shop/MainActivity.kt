package com.secondhand.shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.secondhand.shop.screens.auth.LoginScreen
import com.secondhand.shop.screens.auth.RegisterScreen
import com.secondhand.shop.screens.main.MainScreen
import com.secondhand.shop.screens.products.AddProductScreen
import com.secondhand.shop.screens.products.ProductDetailScreen
import com.secondhand.shop.screens.splash.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables edge-to-edge for the status bar integration we built earlier
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                // Main container for the app
                Surface(color = MaterialTheme.colorScheme.background) {

                    // 1. Initialize the root NavController
                    val rootNavController = rememberNavController()

                    // 2. Define the NavHost
                    // This handles high-level transitions like Auth -> Home
                    NavHost(
                        navController = rootNavController,
                        startDestination = "splash"
                    ) {
                        // --- Route: Splash Screen ---
                        composable("splash") {
                            SplashScreen(onNavigateToLogin = {
                                rootNavController.navigate("login") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            })
                        }

                        // --- Route: Login Screen ---
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    rootNavController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    rootNavController.navigate("register")
                                },
                                onNavigateToForgotPwd = {
                                    // Future implementation
                                })
                        }

                        // --- Route: Register Screen ---
                        composable("register") {
                            RegisterScreen(
                                onNavigateBack = {
                                    rootNavController.popBackStack()
                                },
                                onRegisterSuccess = {
                                    rootNavController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- Route: Main App ---
                        // MainScreen contains the Bottom Bar and its own internal NavHost
                        composable("main") {
                            MainScreen(rootNavController = rootNavController)
                        }

                        // --- Route: Product Detail (Global) ---
                        // We place this here so it can overlap the bottom navigation bar
                        composable("product_detail") {
                            ProductDetailScreen(
                                onBack = { rootNavController.popBackStack() },
                                onChatClicked = {
                                    // Navigate to the chat tab inside MainScreen
                                    // or a global chat detail if preferred
                                    rootNavController.navigate("main") {
                                        // This can be adjusted to open chat directly
                                    }
                                }
                            )
                        }

                        // --- Route: Add Product (Global) ---
                        composable("add_product") {
                            AddProductScreen(
                                onBack = { rootNavController.popBackStack() },
                                onPostSuccess = { rootNavController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}