package com.secondhand.shop.screens.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppBottomBar(val route: String, val icon: ImageVector, val label: String) {
    object Home : AppBottomBar("home", Icons.Default.Home, "Home")
    object Favorites : AppBottomBar("favorites", Icons.Default.Favorite, "Favorites")
    object Chat : AppBottomBar("chat", Icons.Default.Chat, "Chat")
    object Sell : AppBottomBar("sell", Icons.Default.AddCircle, "Sell")
    object Profile : AppBottomBar("profile", Icons.Default.Person, "Profile")
}