package com.secondhand.shop.main

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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.secondhand.shop.R

/**
 * 1. Data Structure for Bottom Navigation
 */
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home_content", Icons.Default.Home, "Home")
    object Favorites : BottomNavItem("favorites", Icons.Default.Favorite, "Favorites")
    object Sell : BottomNavItem("sell", Icons.Default.AddCircle, "Sell")
    object Chat : BottomNavItem("chat", Icons.Default.Chat, "Chat")
    object Profile : BottomNavItem("profile_content", Icons.Default.AccountCircle, "Profile")
}

/**
 * 2. Main Container with Green Navigation and Search-Filter Routing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // Theme Colors
    val ecoGreen = Color(0xFF4CAF50)
    val darkEcoGreen = Color(0xFF388E3C)
    val white = Color.White

    // Check current route to hide/show Top/Bottom bars on specific screens
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        // --- TOP APP BAR (GREEN) ---
        // Hide Top Bar if we are in Search & Filter for a cleaner full-screen look
        topBar = {
            if (currentRoute != "search_filter") {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.mipmap.logo_with_bg),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Second-Hand Shop",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = white
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(BottomNavItem.Profile.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                tint = white,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ecoGreen,
                        titleContentColor = white,
                        actionIconContentColor = white
                    )
                )
            }
        },
        // --- BOTTOM NAVIGATION BAR (GREEN) ---
        bottomBar = {
            // Hide Bottom Bar when searching to give more space for filters
            if (currentRoute != "search_filter") {
                NavigationBar(
                    containerColor = ecoGreen,
                    tonalElevation = 8.dp
                ) {
                    val currentDestination = navBackStackEntry?.destination

                    val items = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Favorites,
                        BottomNavItem.Sell,
                        BottomNavItem.Chat
                    )

                    items.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    color = if (isSelected) white else white.copy(alpha = 0.7f)
                                )
                            },
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = white,
                                unselectedIconColor = white.copy(alpha = 0.7f),
                                indicatorColor = darkEcoGreen
                            ),
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // --- CONTENT AREA (Routing) ---
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Main Content Screens
            composable(BottomNavItem.Home.route) {
                HomeScreen(onNavigateToSearch = {
                    navController.navigate("search_filter")
                })
            }

            // Search and Filter Screen
            composable("search_filter") {
                SearchFilterScreen(onBack = {
                    navController.popBackStack()
                })
            }

            // Other Navigation Destinations
            composable(BottomNavItem.Favorites.route) { PlaceholderScreen("My Favorites") }
            composable(BottomNavItem.Sell.route) { PlaceholderScreen("Sell an Item") }
            composable(BottomNavItem.Chat.route) { PlaceholderScreen("Messages") }
            composable(BottomNavItem.Profile.route) { PlaceholderScreen("User Profile") }
        }
    }
}

/**
 * 3. Simple Placeholder Screen
 */
@Composable
fun PlaceholderScreen(name: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF7F7F7)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Gray
            )
        }
    }
}