package com.secondhand.shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.secondhand.shop.screens.auth.LoginScreen
import com.secondhand.shop.screens.auth.RegisterScreen
import com.secondhand.shop.screens.main.BottomNavItem
import com.secondhand.shop.screens.main.MainScreen
import com.secondhand.shop.screens.products.AddProductScreen
import com.secondhand.shop.screens.products.FavoritesScreen
import com.secondhand.shop.screens.products.ProductDetailScreen
import com.secondhand.shop.screens.splash.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {

                    // 1. Initialize the NavController for top-level navigation
                    val navController = rememberNavController()

                    // 2. Define the NavHost with routes
                    NavHost(
                        navController = navController,
                        startDestination = "splash" // App entry point
                    ) {
                        // --- Route: Splash Screen ---
                        composable("splash") {
                            SplashScreen(onNavigateToLogin = {
                                // Navigate to login and remove splash so user can't go back to it
                                navController.navigate("login") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            })
                        }

                        // --- Route: Login Screen ---
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    // Navigate to the Main Screen (which contains the Bottom Bar)
                                    navController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                },
                                onNavigateToForgotPwd = {
                                    // Future: navController.navigate("forgot_password")
                                })
                        }

                        // --- Route: Register Screen ---
                        composable("register") {
                            RegisterScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onRegisterSuccess = {
                                    // Navigate to Main after successful registration
                                    navController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- Route: Main App (Home, Sell, Chat, Profile) ---
                        // This route loads the MainScreen which has its own internal NavHost for the BottomBar
                        composable("main") {
                            MainScreen(rootNavController = navController)
                        }

                        // Inside your NavHost block
                        composable("favorites") {
                            FavoritesScreen(
                                onProductClick = { productId ->
                                    navController.navigate("product_detail/$productId")
                                }
                            )
                        }

                        composable("product_detail") {
                            ProductDetailScreen(
                                onBack = { navController.popBackStack() },
                                onChatClicked = { navController.navigate("chat_detail") }
                            )
                        }

                        composable("add_product") {
                            AddProductScreen(
                                onBack = { navController.popBackStack() },
                                onPostSuccess = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}