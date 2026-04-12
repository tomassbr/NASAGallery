package kmp.android.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

// Matches Figma navigation: Home / Favorites / Profile
enum class NavBarTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    HOME(route = "home_tab", label = "Home", icon = Icons.Default.Home),
    FAVORITES(route = "favorites_tab", label = "Favorites", icon = Icons.Default.Favorite),
    PROFILE(route = "profile_tab", label = "Profile", icon = Icons.Default.Person),
}
