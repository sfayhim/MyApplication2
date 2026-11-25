package com.example.myapplication.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.about.AboutScreen
import com.example.myapplication.ui.components.PierreFabreDrawer
import com.example.myapplication.ui.dashboard.DashboardScreen
import com.example.myapplication.ui.detail.DetailScreen
import com.example.myapplication.ui.login.LoginScreen
import com.example.myapplication.ui.mammal.MammalScreen
import com.example.myapplication.ui.results.ResultsScreen
import com.example.myapplication.ui.search.SearchScreen
import com.example.myapplication.ui.favorites.FavoritesScreen
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "login"

    PierreFabreDrawer(
        drawerState = drawerState,
        scope = scope,
        currentRoute = currentRoute,
        onNavigate = { route ->
            navController.navigate(route) {
                popUpTo("dashboard") { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        },
        onLogout = {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    ) {
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("dashboard") {
                DashboardScreen(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onNavigateToSearch = { query ->
                        val encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
                        navController.navigate("search?query=$encoded")
                    },
                    onNavigateToDetail = { type, id ->
                        val encodedId = URLEncoder.encode(id, StandardCharsets.UTF_8.toString())
                        navController.navigate("detail/$type/$encodedId")
                    }
                )
            }

            composable(
                route = "search?query={query}",
                arguments = listOf(navArgument("query") { defaultValue = "" })
            ) { backStackEntry ->
                val query = backStackEntry.arguments?.getString("query") ?: ""
                SearchScreen(
                    initialQuery = query,
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { type, id ->
                        val encodedId = URLEncoder.encode(id, StandardCharsets.UTF_8.toString())
                        navController.navigate("detail/$type/$encodedId")
                    },
                    onNavigateToResults = { q ->
                        val encoded = URLEncoder.encode(q, StandardCharsets.UTF_8.toString())
                        navController.navigate("results?query=$encoded")
                    }
                )
            }

            composable(
                route = "results?query={query}",
                arguments = listOf(navArgument("query") { defaultValue = "" })
            ) { backStackEntry ->
                val query = backStackEntry.arguments?.getString("query") ?: ""
                ResultsScreen(
                    query = query,
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { type, id ->
                        val encodedId = URLEncoder.encode(id, StandardCharsets.UTF_8.toString())
                        navController.navigate("detail/$type/$encodedId")
                    }
                )
            }

            composable(
                route = "detail/{itemType}/{itemId}",
                arguments = listOf(
                    navArgument("itemType") { type = NavType.StringType },
                    navArgument("itemId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val itemType = backStackEntry.arguments?.getString("itemType") ?: ""
                val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                DetailScreen(
                    itemType = itemType,
                    itemId = itemId,
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("mammal") {
                MammalScreen(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("about") {
                AboutScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("favorites") {
                FavoritesScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { type, id ->
                        val encodedId = URLEncoder.encode(id, StandardCharsets.UTF_8.toString())
                        navController.navigate("detail/$type/$encodedId")
                    }
                )
            }
        }
    }
}
