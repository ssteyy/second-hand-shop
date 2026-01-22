package com.secondhand.shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val rootNavController = rememberNavController()
                    val profileViewModel: ProfileViewModel = viewModel()

                    // The Root NavHost manages the entire app's lifecycle
                    NavHost(
                        navController = rootNavController,
                        startDestination = "splash"
                    ) {

                        // 1. Splash Screen: Decides if we go to Login or Home
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

                        // 2. Auth Flow: Login
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    rootNavController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = { rootNavController.navigate("register") },
                                onNavigateToForgotPwd = { /* Implement if needed */ }
                            )
                        }

                        // 3. Auth Flow: Register
                        composable("register") {
                            RegisterScreen(
                                onNavigateBack = { rootNavController.popBackStack() },
                                onRegisterSuccess = {
                                    rootNavController.navigate("main") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 4. Main App: Holds the Bottom Navigation and Tabs
                        composable("main") {
                            MainScreen(
                                rootNavController = rootNavController,
                                profileViewModel = profileViewModel
                            )
                        }

                        // 5. Product Details: Global route to allow viewing from Home or My Listings
                        // Inside your NavHost in MainScreen.kt
                        composable(
                            route = "product_detail/{productId}",
                            arguments = listOf(navArgument("productId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId") ?: ""

                            ProductDetailScreen(
                                productId = productId,
                                onBack = { rootNavController.popBackStack() },
                                onChatClicked = { /* chat logic */ },
                                onViewProfile = { sellerId ->
                                    // 3. Perform the actual navigation here using the CORRECT controller
                                    rootNavController.navigate("seller_profile/$sellerId/Seller Name/seller@email.com")
                                }
                            )
                        }

                        // 6. Add/Edit Product: Full-screen overlay (hides bottom bar)
                        composable(
                            route = "add_product?productId={productId}",
                            arguments = listOf(navArgument("productId") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId")
                            AddProductScreen(
                                productId = productId,
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