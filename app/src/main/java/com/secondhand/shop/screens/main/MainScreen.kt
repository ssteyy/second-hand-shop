package com.secondhand.shop.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
 * 1. Data Structure for Bottom Navigation (Profile Added Here)
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

    Scaffold(
        topBar = {
            // Only show TopBar on main tabs, hide on sub-screens like search or manage listings
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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = ecoGreen)
                )
            }
        },
        bottomBar = {
            // Hide bottom bar when in the search filter screen
            if (currentRoute != "search_filter") {
                NavigationBar(containerColor = ecoGreen, tonalElevation = 8.dp) {
                    val currentDestination = navBackStackEntry?.destination

                    val items = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Favorites,
                        BottomNavItem.Sell,
                        BottomNavItem.Chat,
                        BottomNavItem.Profile // Profile is now a tab
                    )

                    items.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 10.sp,
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
                                if (item == BottomNavItem.Sell) {
                                    rootNavController.navigate("add_product")
                                } else {
                                    internalNavController.navigate(item.route) {
                                        popUpTo(internalNavController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
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
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    onNavigateToSearch = { internalNavController.navigate("search_filter") },
                    onProductClick = { rootNavController.navigate("product_detail") }
                )
            }

            composable(BottomNavItem.Favorites.route) {
                FavoritesScreen(
                    onProductClick = { rootNavController.navigate("product_detail") }
                )
            }

            composable(BottomNavItem.Sell.route) {
                LaunchedEffect(Unit) {
                    rootNavController.navigate("add_product")
                }
            }

            composable(BottomNavItem.Chat.route) { PlaceholderScreen("Messages") }

            composable(BottomNavItem.Profile.route) {
                ProfileScreen(onNavigateToListings = {
                    internalNavController.navigate("manage_listings")
                })
            }

            // Sub-screens
            composable("manage_listings") {
                ManageListingsScreen(
                    onBack = { internalNavController.popBackStack() },
                    onEditProduct = { rootNavController.navigate("add_product") }
                )
            }

            composable("search_filter") {
                SearchFilterScreen(onBack = { internalNavController.popBackStack() })
            }
        }
    }
}

@Composable
fun ProfileScreen(onNavigateToListings: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = Color(0xFFF0F0F0)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.padding(20.dp),
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("My Account", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Text("user@example.com", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        // Profile Menu Item
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onNavigateToListings() },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.List, null, tint = Color(0xFF4CAF50))
                Spacer(modifier = Modifier.width(16.dp))
                Text("Manage My Listings", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
            }
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