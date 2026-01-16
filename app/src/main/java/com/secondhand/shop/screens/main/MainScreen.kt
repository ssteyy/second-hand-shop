package com.secondhand.shop.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.secondhand.shop.R
import com.secondhand.shop.screens.products.FavoritesScreen
import com.secondhand.shop.screens.products.ManageListingsScreen

/**
 * 1. Data Structure for Bottom Navigation
 */
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home_content", Icons.Default.Home, "Home")
    object Favorites : BottomNavItem("favorites", Icons.Default.Favorite, "Favorites")
    object Sell : BottomNavItem("sell", Icons.Default.AddCircle, "Sell")
    object Chat : BottomNavItem("chat", Icons.Default.Chat, "Chat")
}

/**
 * 2. Main Container
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(rootNavController: NavHostController) {
    // internalNavController manages tabs (Home, Favorites, Chat, Profile)
    val internalNavController = rememberNavController()

    val ecoGreen = Color(0xFF4CAF50)
    val darkEcoGreen = Color(0xFF388E3C)
    val white = Color.White

    val navBackStackEntry by internalNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            // Hide TopBar for specific internal sub-screens if needed
            if (currentRoute != "search_filter" && currentRoute != "manage_listings") {
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
                                text = "Second-Hand Shop",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = white
                            )
                        }
                    },
                    actions = {
                        // Access Profile via Top Bar
                        IconButton(onClick = {
                            internalNavController.navigate("profile_content") {
                                popUpTo(internalNavController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }) {
                            Icon(Icons.Default.AccountCircle, "Profile", tint = white, modifier = Modifier.size(28.dp))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = ecoGreen)
                )
            }
        },
        bottomBar = {
            if (currentRoute != "search_filter") {
                NavigationBar(containerColor = ecoGreen, tonalElevation = 8.dp) {
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
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, color = if (isSelected) white else white.copy(alpha = 0.7f)) },
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
                    onProductClick = { rootNavController.navigate("product_detail") }
                )
            }

            // --- FAVORITES TAB ---
            composable(BottomNavItem.Favorites.route) {
                // Pass the click logic to push to Product Detail using rootNavController
                FavoritesScreen(
                    onProductClick = { productId ->
                        rootNavController.navigate("product_detail")
                        // Note: If your detail route needs an ID, use "product_detail/$productId"
                    }
                )
            }

            // --- SELL TAB (Redirect) ---
            composable(BottomNavItem.Sell.route) {
                LaunchedEffect(Unit) {
                    rootNavController.navigate("add_product")
                }
            }

            // --- CHAT TAB ---
            composable(BottomNavItem.Chat.route) { PlaceholderScreen("Messages") }

            // --- PROFILE TAB ---
            composable("profile_content") {
                ProfileScreen(onNavigateToListings = {
                    internalNavController.navigate("manage_listings")
                })
            }

            // --- MANAGE LISTINGS (Sub-screen) ---
            composable("manage_listings") {
                ManageListingsScreen(
                    onBack = { internalNavController.popBackStack() },
                    onEditProduct = { rootNavController.navigate("add_product") }
                )
            }

            // --- SEARCH FILTER (Sub-screen) ---
            composable("search_filter") {
                SearchFilterScreen(onBack = { internalNavController.popBackStack() })
            }
        }
    }
}

@Composable
fun ProfileScreen(onNavigateToListings: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(80.dp), tint = Color.Gray)
        Text("My Account", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToListings,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.List, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Manage My Listings")
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF7F7F7)) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = name, style = MaterialTheme.typography.headlineMedium, color = Color.Gray)
        }
    }
}