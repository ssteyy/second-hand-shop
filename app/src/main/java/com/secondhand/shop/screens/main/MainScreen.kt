package com.secondhand.shop.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.secondhand.shop.R
import com.secondhand.shop.screens.chat.ChatDetailScreen
import com.secondhand.shop.screens.chat.ChatListScreen
import com.secondhand.shop.screens.notifications.NotificationsScreen
import com.secondhand.shop.screens.products.FavoritesScreen
import com.secondhand.shop.screens.products.ManageListingsScreen
import com.secondhand.shop.screens.products.ProductDetailScreen
import com.secondhand.shop.screens.profile.EditProfileScreen
import com.secondhand.shop.screens.profile.SettingsScreen
import com.secondhand.shop.screens.profile.UserScreen

/**
 * 1. Data Structure for Bottom Navigation
 */
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home_content", Icons.Default.Home, "Home")
    object Favorites : BottomNavItem("favorites", Icons.Default.Favorite, "Saved")
    object Sell : BottomNavItem("sell", Icons.Default.AddCircle, "Sell")
    object Chat : BottomNavItem("chat", Icons.Default.Chat, "Chat")
    object Profile : BottomNavItem("profile_content", Icons.Default.Person, "Profile")
}

/**
 * 2. Main Container
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(rootNavController: NavHostController) {
    val internalNavController = rememberNavController()
    val ecoGreen = Color(0xFF4CAF50)
    val darkEcoGreen = Color(0xFF388E3C)
    val white = Color.White

    val navBackStackEntry by internalNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define routes where UI components should be hidden
    val isChatDetail = currentRoute?.startsWith("chat_detail") == true
    val isProductDetail = currentRoute == "product_detail"

    val hideTopBarRoutes = listOf("search_filter", "manage_listings", "edit_profile", "settings")
    val hideBottomBarRoutes = listOf("search_filter", "manage_listings", "edit_profile", "settings")

    Scaffold(
        topBar = {
            // Show TopBar only on main tabs, hide on sub-screens or detail screens
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
                            Text("Second-Hand Shop", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = white)
                        }
                    },
                    actions = {
                        IconButton(onClick = { internalNavController.navigate("notifications") }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = white)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = ecoGreen)
                )
            }
        },
        bottomBar = {
            // Hide Navigation Bar on Detail screens and Search
            if (currentRoute !in hideBottomBarRoutes && !isChatDetail && !isProductDetail) {
                NavigationBar(containerColor = ecoGreen, tonalElevation = 8.dp) {
                    val currentDestination = navBackStackEntry?.destination
                    val items = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Favorites,
                        BottomNavItem.Sell,
                        BottomNavItem.Chat,
                        BottomNavItem.Profile
                    )

                    items.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, fontSize = 10.sp, color = if (isSelected) white else white.copy(alpha = 0.7f)) },
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
            // --- HOME TAB ---
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    onNavigateToSearch = { internalNavController.navigate("search_filter") },
                    onProductClick = { internalNavController.navigate("product_detail") }
                )
            }

            // --- SAVED TAB ---
            composable(BottomNavItem.Favorites.route) {
                FavoritesScreen() { internalNavController.popBackStack() }
            }

            // --- CHAT TAB ---
            composable(BottomNavItem.Chat.route) {
                ChatListScreen(onChatClick = { userName ->
                    internalNavController.navigate("chat_detail/$userName")
                })
            }

            // --- PROFILE TAB ---
            composable(BottomNavItem.Profile.route) {
                UserScreen(
                    onNavigateToEdit = { internalNavController.navigate("edit_profile") },
                    onNavigateToListings = { internalNavController.navigate("manage_listings") },
                    onNavigateToSettings = { internalNavController.navigate("settings") },
                    onNavigateToFavorites = { internalNavController.navigate(BottomNavItem.Favorites.route) }
                )
            }

            // --- DETAIL & SUB-SCREENS ---

            composable("product_detail") {
                ProductDetailScreen(
                    onBack = { internalNavController.popBackStack() },
                    onChatClicked = {
                        // You can pass a specific name here or a dynamic ID
                        internalNavController.navigate("chat_detail/Sok Nimol")
                    }
                )
            }

            composable("chat_detail/{userName}") { backStackEntry ->
                val userName = backStackEntry.arguments?.getString("userName") ?: "User"
                ChatDetailScreen(
                    userName = userName,
                    onBack = { internalNavController.popBackStack() }
                )
            }

            composable("manage_listings") {
                ManageListingsScreen(
                    onBack = { internalNavController.popBackStack() },
                    onEditProduct = { rootNavController.navigate("add_product") }
                )
            }

            composable("search_filter") {
                SearchFilterScreen(onBack = { internalNavController.popBackStack() })
            }

            composable("notifications") {
                NotificationsScreen(onBack = { internalNavController.popBackStack() })
            }

            composable("edit_profile") {
                EditProfileScreen(onBack = { internalNavController.popBackStack() })
            }

            composable("settings") {
                SettingsScreen(
                    onBack = { internalNavController.popBackStack() },
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        rootNavController.navigate("login") {
                            popUpTo(0)
                        }
                    }
                )
            }
        }
    }
}