package kmp.android.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kmp.android.apod.navigation.apodNavGraph
import kmp.android.profile.navigation.profileNavGraph
import kmp.android.saved.navigation.savedNavGraph
import kmp.android.shared.style.NasaColor

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val bottomNavController = rememberNavController()

    Scaffold(
        modifier = modifier,
        backgroundColor = NasaColor.Background,
        bottomBar = { NasaBottomBar(bottomNavController) },
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = NavBarTab.HOME.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            apodNavGraph(bottomNavController)
            savedNavGraph(bottomNavController)
            profileNavGraph(bottomNavController)
        }
    }
}

@Composable
private fun NasaBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomNavigation(
        backgroundColor = NasaColor.Surface,
        contentColor = NasaColor.OnSurface,
    ) {
        NavBarTab.entries.forEach { tab ->
            val selected = currentDestination?.hierarchy?.any { it.route?.startsWith(tab.route) == true } == true
            BottomNavigationItem(
                selected = selected,
                onClick = {
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
                selectedContentColor = NasaColor.Primary,
                unselectedContentColor = NasaColor.OnSurfaceVariant,
            )
        }
    }
}
