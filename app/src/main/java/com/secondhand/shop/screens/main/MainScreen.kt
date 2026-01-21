package com.secondhand.shop.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.secondhand.shop.R
import com.secondhand.shop.screens.chat.ChatDetailScreen
import com.secondhand.shop.screens.chat.ChatListScreen
import com.secondhand.shop.screens.notifications.NotificationsScreen
import com.secondhand.shop.screens.products.*
import com.secondhand.shop.screens.profile.*

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home_content", Icons.Default.Home, "Home")
    object Favorites : BottomNavItem("favorites", Icons.Default.Favorite, "Saved")
    object Sell : BottomNavItem("sell", Icons.Default.AddCircle, "Sell")
    object Chat : BottomNavItem("chat", Icons.AutoMirrored.Filled.Chat, "Chat")
    object Profile : BottomNavItem("profile_content", Icons.Default.Person, "Profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    rootNavController: NavHostController,
    profileViewModel: ProfileViewModel
) {
    val internalNavController = rememberNavController()
    val ecoGreen = Color(0xFF4CAF50)
    val darkEcoGreen = Color(0xFF388E3C)
    val white = Color.White

    val navBackStackEntry by internalNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Routes where bars are hidden
    val hideTopBarRoutes = listOf("search_filter", "manage_listings", "edit_profile", "settings")
    val hideBottomBarRoutes = listOf("search_filter", "manage_listings", "edit_profile", "settings")

    val isChatDetail = currentRoute?.startsWith("chat_detail") == true
    val isProductDetail = currentRoute?.startsWith("product_detail") == true

    Scaffold(
        topBar = {
            if (currentRoute !in hideTopBarRoutes && !isChatDetail && !isProductDetail) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.mipmap.logo_with_bg),
                                contentDescription = "App Logo",
                                modifier = Modifier.size(32.dp).clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Second-Hand Shop",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = white
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { internalNavController.navigate("notifications") }) {
                            Icon(Icons.Default.Notifications, "Notifications", tint = white)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = ecoGreen)
                )
            }
        },
        bottomBar = {
            if (currentRoute !in hideBottomBarRoutes && !isChatDetail && !isProductDetail) {
                NavigationBar(containerColor = ecoGreen) {
                    val items = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Favorites,
                        BottomNavItem.Sell,
                        BottomNavItem.Chat,
                        BottomNavItem.Profile
                    )

                    items.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, fontSize = 10.sp) },
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = white,
                                unselectedIconColor = white.copy(alpha = 0.7f),
                                indicatorColor = darkEcoGreen
                            ),
                            onClick = {
                                if (item == BottomNavItem.Sell) {
                                    rootNavController.navigate("add_product")
                                } else {
                                    internalNavController.navigate(item.route) {
                                        popUpTo(internalNavController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = internalNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Home Screen
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    onNavigateToSearch = { internalNavController.navigate("search_filter") },
                    onProductClick = { productId: String ->
                        internalNavController.navigate("product_detail/$productId")
                    }
                )
            }

            // Favorites Screen
            composable(BottomNavItem.Favorites.route) {
                FavoritesScreen(
                    onProductClick = { productId: String ->
                        internalNavController.navigate("product_detail/$productId")
                    }
                )
            }

            // Chat List Screen
            composable(BottomNavItem.Chat.route) {
                ChatListScreen { userName: String ->
                    internalNavController.navigate("chat_detail/$userName")
                }
            }

            // Profile Screen
            composable(BottomNavItem.Profile.route) {
                UserScreen(
                    viewModel = profileViewModel,
                    onNavigateToEdit = { internalNavController.navigate("edit_profile") },
                    onNavigateToListings = { internalNavController.navigate("manage_listings") },
                    onNavigateToSettings = { internalNavController.navigate("settings") },
                    onNavigateToFavorites = { internalNavController.navigate(BottomNavItem.Favorites.route) }
                )
            }

            // Product Detail Screen
            composable(
                route = "product_detail/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailScreen(
                    productId = productId,
                    onBack = { internalNavController.popBackStack() },
                    onChatClicked = { /* Optional chat logic */ }
                )
            }

            // Chat Detail Screen
            composable(
                route = "chat_detail/{userName}",
                arguments = listOf(navArgument("userName") { type = NavType.StringType })
            ) { backStackEntry ->
                val userName = backStackEntry.arguments?.getString("userName") ?: "User"
                ChatDetailScreen(
                    userName = userName,
                    onBack = { internalNavController.popBackStack() }
                )
            }

            // Manage Listings Screen
            composable("manage_listings") {
                ManageListingsScreen(
                    onBack = { internalNavController.popBackStack() },
                    onEditProduct = { productId: String ->
                        rootNavController.navigate("add_product?productId=$productId")
                    },
                    onProductClick = { productId: String ->
                        internalNavController.navigate("product_detail/$productId")
                    }
                )
            }

            // Other screens
            composable("search_filter") { SearchFilterScreen(onBack = { internalNavController.popBackStack() }) }
            composable("notifications") { NotificationsScreen(onBack = { internalNavController.popBackStack() }) }
            composable("edit_profile") { EditProfileScreen(profileViewModel = profileViewModel, onBack = { internalNavController.popBackStack() }) }
            composable("settings") {
                SettingsScreen(
                    profileViewModel = profileViewModel,
                    onBack = { internalNavController.popBackStack() },
                    onLogoutSuccess = {
                        FirebaseAuth.getInstance().signOut()
                        rootNavController.navigate("login") { popUpTo(0) }
                    }
                )
            }
        }
    }
}
