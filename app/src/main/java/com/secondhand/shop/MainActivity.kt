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
import com.secondhand.shop.screens.chat.ChatDetailScreen
import com.secondhand.shop.screens.splash.SplashScreen
import com.secondhand.shop.screens.profile.ProfileViewModel
import java.net.URLDecoder

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val rootNavController = rememberNavController()
                    val profileViewModel: ProfileViewModel = viewModel()

                    NavHost(
                        navController = rootNavController,
                        startDestination = "splash"
                    ) {
                        // 1. Splash Screen
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

                        // 2. Login Screen
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

                        // 3. Register Screen
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

                        // 4. Main App Screen
                        composable("main") {
                            MainScreen(
                                rootNavController = rootNavController,
                                profileViewModel = profileViewModel
                            )
                        }

                        // 5. Product Detail
                        composable(
                            route = "product_detail/{productId}",
                            arguments = listOf(navArgument("productId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId") ?: ""
                            ProductDetailScreen(
                                productId = productId,
                                onBack = { rootNavController.popBackStack() },
                                onChatClicked = { chatId: String, sellerName: String ->
                                    // Encoding prevents issues with spaces or special characters in names
                                    val encodedName = java.net.URLEncoder.encode(sellerName, "UTF-8")
                                    rootNavController.navigate("chat_detail/$chatId/$encodedName")
                                },
                                onViewProfile = { sellerId ->
                                    rootNavController.navigate("seller_profile/$sellerId")
                                },
                                onManageListings = {
                                    rootNavController.navigate("manage_listings")
                                }
                            )
                        }

                        // 6. Add/Edit Product
                        composable(
                            route = "add_product?productId={productId}",
                            arguments = listOf(
                                navArgument("productId") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId")
                            AddProductScreen(
                                productId = productId,
                                onBack = { rootNavController.popBackStack() },
                                onPostSuccess = { rootNavController.popBackStack() }
                            )
                        }

                        // 7. Chat Detail (Fixed parameter passing)
                        composable(
                            route = "chat_detail/{chatId}/{userName}",
                            arguments = listOf(
                                navArgument("chatId") { type = NavType.StringType },
                                navArgument("userName") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                            val rawUserName = backStackEntry.arguments?.getString("userName") ?: "User"
                            // Decode the name back to normal text (e.g., "John%20Doe" -> "John Doe")
                            val userName = URLDecoder.decode(rawUserName, "UTF-8")

                            ChatDetailScreen(
                                chatId = chatId, // Passed the missing parameter here
                                userName = userName,
                                onBack = { rootNavController.popBackStack() }
                            )
                        }


                    }
                }
            }
        }
    }
}