package com.example.pawsitive_app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.NavGraph.Companion.findStartDestination

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object News : Screen("news", "News", Icons.Default.Newspaper)
    object Adoption : Screen("adoption", "Adoption", Icons.Default.Favorite)
    object Donate : Screen("donate", "Donate", Icons.Default.AttachMoney)
    object More : Screen("more", "More", Icons.Default.MoreHoriz)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.News,
        Screen.Adoption,
        Screen.Donate,
        Screen.More
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = androidx.compose.ui.graphics.Color(0xFFE53935),
                            selectedTextColor = androidx.compose.ui.graphics.Color(0xFFE53935)
                        ),
                        onClick = {
                            navController.navigate(screen.route) {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.News.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.News.route) { NewsScreen() }
            composable(Screen.Adoption.route) { AdoptionScreen() }
            composable(Screen.Donate.route) { DonateScreen() }
            composable(Screen.More.route) { MoreScreen() }
        }
    }
}
