package com.secondhand.shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.secondhand.shop.screens.auth.LoginScreen
import com.secondhand.shop.screens.auth.RegisterScreen
import com.secondhand.shop.screens.main.MainScreen
import com.secondhand.shop.screens.products.AddProductScreen
import com.secondhand.shop.screens.products.ProductDetailScreen
import com.secondhand.shop.screens.splash.SplashScreen
import com.secondhand.shop.screens.profile.ProfileViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables edge-to-edge UI
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {

                    // Root NavController
                    val rootNavController = rememberNavController()

                    // ProfileViewModel for shared profile screens
                    val profileViewModel: ProfileViewModel = viewModel()

                    // Root NavHost
                    NavHost(
                        navController = rootNavController,
                        startDestination = "splash"
                    ) {

                        // --- Splash Screen ---
                        composable("splash") {
                            SplashScreen(
                                onNavigateToLogin = {
                                    rootNavController.navigate("login") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                },
                                onNavigateToMain = {
                                    rootNavController.navigate("main") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- Login Screen ---
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
                                    // TODO: Implement forgot password
                                }
                            )
                        }

                        // --- Register Screen ---
                        composable("register") {
                            RegisterScreen(
                                onNavigateBack = {
                                    rootNavController.popBackStack()
                                },
                                onRegisterSuccess = {
                                    rootNavController.navigate("main") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- Main App ---
                        composable("main") {
                            MainScreen(
                                rootNavController = rootNavController,
                                profileViewModel = profileViewModel
                            )
                        }

                        // --- Product Detail (Global) ---
                        composable("product_detail") {
                            ProductDetailScreen(
                                onBack = { rootNavController.popBackStack() },
                                onChatClicked = {
                                    // Navigate back to main for now
                                    rootNavController.navigate("main")
                                }
                            )
                        }

                        // --- Add Product (Global) ---
                        composable("add_product") {
                            AddProductScreen(
                                onBack = { rootNavController.popBackStack() },
                                onPostSuccess = { rootNavController.popBackStack() },
                                onNotificationsClick = {
                                    // 1. Close the Add Product screen
                                    rootNavController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
