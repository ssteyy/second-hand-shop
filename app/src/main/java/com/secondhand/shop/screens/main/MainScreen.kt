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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.secondhand.shop.R
import com.secondhand.shop.model.Chat
import com.secondhand.shop.model.Product
import com.secondhand.shop.screens.chat.ChatDetailScreen
import com.secondhand.shop.screens.chat.ChatListScreen
import com.secondhand.shop.screens.notifications.NotificationsScreen
import com.secondhand.shop.screens.products.*
import com.secondhand.shop.screens.profile.*
import java.net.URLEncoder

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

    // --- Notification Badge Logic ---
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var hasUnreadMessages by remember { mutableStateOf(false) }

    LaunchedEffect(currentUserId) {
        if (currentUserId.isEmpty()) return@LaunchedEffect
        val db = Firebase.firestore
        // Listen to all chats where the user is a member
        db.collection("chats")
            .whereArrayContains("members", currentUserId)
            .addSnapshotListener { snapshot, _ ->
                val chats = snapshot?.toObjects(Chat::class.java) ?: emptyList()
                // Check if any chat has unread messages for this specific user
                hasUnreadMessages = chats.any { it.unreadCountForUser(currentUserId) > 0 }
            }
    }
    // --------------------------------

    val hideTopBarRoutes = listOf("search_filter", "manage_listings", "edit_profile", "settings", "favorites", "notifications", "help_center", "privacy_policy")
    val hideBottomBarRoutes = listOf("search_filter", "manage_listings", "edit_profile", "settings", "help_center", "privacy_policy")

    val isChatDetail = currentRoute?.startsWith("chat_detail") == true
    val isProductDetail = currentRoute?.startsWith("product_detail") == true
    val isSellerProfile = currentRoute?.startsWith("seller_profile") == true

    Scaffold(
        topBar = {
            if (currentRoute !in hideTopBarRoutes && !isChatDetail && !isProductDetail && !isSellerProfile) {
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
                            // ✅ Red Dot Badge Implementation
                            BadgedBox(
                                badge = {
                                    if (hasUnreadMessages) {
                                        Badge(
                                            containerColor = Color.Red,
                                            modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                                        )
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, "Notifications", tint = white)
                            }
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
                        BottomNavItem.Home, BottomNavItem.Favorites,
                        BottomNavItem.Sell, BottomNavItem.Chat, BottomNavItem.Profile
                    )
                    items.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            icon = {
                                // Optional: You can also add a badge to the Chat icon in the bottom bar
                                if (item == BottomNavItem.Chat) {
                                    BadgedBox(badge = { if (hasUnreadMessages) Badge(containerColor = Color.Red) }) {
                                        Icon(item.icon, contentDescription = item.label)
                                    }
                                } else {
                                    Icon(item.icon, contentDescription = item.label)
                                }
                            },
                            label = { Text(item.label, fontSize = 10.sp, color = Color.White) },
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
            composable(BottomNavItem.Home.route) {
                HomeScreen(onProductClick = { id -> internalNavController.navigate("product_detail/$id") })
            }

            composable(BottomNavItem.Favorites.route) {
                FavoritesScreen(
                    onBack = { internalNavController.popBackStack() },
                    onProductClick = { id -> internalNavController.navigate("product_detail/$id") }
                )
            }

            composable(BottomNavItem.Chat.route) {
                ChatListScreen { chatId, userName ->
                    val encodedName = URLEncoder.encode(userName, "UTF-8")
                    internalNavController.navigate("chat_detail/$chatId/$encodedName")
                }
            }

            composable(BottomNavItem.Profile.route) {
                UserScreen(
                    viewModel = profileViewModel,
                    onNavigateToEdit = { internalNavController.navigate("edit_profile") },
                    onNavigateToListings = { internalNavController.navigate("manage_listings") },
                    onNavigateToSettings = { internalNavController.navigate("settings") },
                    onNavigateToHelp = { internalNavController.navigate("help_center") },
                    onNavigatorToPrivancy = { internalNavController.navigate("privacy_policy") },
                    onNavigateToFavorites = { internalNavController.navigate(BottomNavItem.Favorites.route) }
                )
            }

            composable("product_detail/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailScreen(
                    productId = productId,
                    onBack = { internalNavController.popBackStack() },
                    onChatClicked = { chatId, sellerName ->
                        val encodedName = URLEncoder.encode(sellerName, "UTF-8")
                        internalNavController.navigate("chat_detail/$chatId/$encodedName")
                    },
                    onViewProfile = { sellerId -> internalNavController.navigate("seller_profile/$sellerId") },
                    onManageListings = { internalNavController.navigate("manage_listings") }
                )
            }

            composable(
                route = "seller_profile/{sellerId}",
                arguments = listOf(navArgument("sellerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val sId = backStackEntry.arguments?.getString("sellerId") ?: ""
                val db = Firebase.firestore

                var name by remember { mutableStateOf("Loading...") }
                var email by remember { mutableStateOf("") }
                var bio by remember { mutableStateOf("") }
                var phone by remember { mutableStateOf("") }
                var imageUrl by remember { mutableStateOf<String?>(null) }
                var products by remember { mutableStateOf<List<Product>>(emptyList()) }
                var isLoading by remember { mutableStateOf(true) }

                LaunchedEffect(sId) {
                    db.collection("users").document(sId).get().addOnSuccessListener { doc ->
                        name = doc.getString("fullName") ?: "Unknown"
                        email = doc.getString("email") ?: ""
                        bio = doc.getString("bio") ?: ""
                        phone = doc.getString("phone") ?: ""
                        imageUrl = doc.getString("profileImage") ?: doc.getString("profileImageUrl")
                    }

                    db.collection("products")
                        .whereEqualTo("sellerId", sId)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            products = snapshot.documents.mapNotNull { doc ->
                                doc.toObject(Product::class.java)?.copy(id = doc.id)
                            }
                            isLoading = false
                        }
                        .addOnFailureListener { isLoading = false }
                }

                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ecoGreen)
                    }
                } else {
                    SellerProfileScreen(
                        sellerName = name,
                        sellerEmail = email,
                        sellerBio = bio,
                        sellerPhone = phone,
                        sellerImageUrl = imageUrl,
                        sellerProducts = products,
                        onBack = { internalNavController.popBackStack() },
                        onProductClick = { id -> internalNavController.navigate("product_detail/$id") },
                        onChatClick = { targetSellerId, targetSellerName ->
                            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                            if (currentUserId.isNotEmpty()) {
                                val chatId = if (currentUserId < targetSellerId)
                                    "${currentUserId}_$targetSellerId"
                                else
                                    "${targetSellerId}_$currentUserId"

                                val encodedName = URLEncoder.encode(targetSellerName, "UTF-8")
                                internalNavController.navigate("chat_detail/$chatId/$encodedName")
                            }
                        }
                    )
                }
            }

            composable("manage_listings") {
                ManageListingsScreen(
                    onBack = { internalNavController.popBackStack() },
                    onEditProduct = { id -> rootNavController.navigate("add_product?productId=$id") },
                    onProductClick = { id -> internalNavController.navigate("product_detail/$id") }
                )
            }

            composable("chat_detail/{chatId}/{userName}", arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("userName") { type = NavType.StringType }
            )) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                val userName = backStackEntry.arguments?.getString("userName") ?: "User"
                ChatDetailScreen(chatId = chatId, userName = userName, onBack = { internalNavController.popBackStack() })
            }

            composable("search_filter") { SearchFilterScreen(onBack = { internalNavController.popBackStack() }) }

            composable("notifications") {
                NotificationsScreen(
                    onBack = { internalNavController.popBackStack() },
                    onChatClick = { chatId ->
                        val encodedName = URLEncoder.encode("Chat", "UTF-8")
                        internalNavController.navigate("chat_detail/$chatId/$encodedName")
                    }
                )
            }

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

            composable("help_center") {
                HelpCenterScreen(
                    onBack = { internalNavController.popBackStack() }
                )
            }

            composable("privacy_policy") {
                PrivacyPolicyScreen(
                    onBack = { internalNavController.popBackStack() }
                )
            }
        }
    }
}