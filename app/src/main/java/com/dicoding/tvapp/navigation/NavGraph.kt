package com.dicoding.tvapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dicoding.tvapp.ui.detail.ShowDetailScreen
import com.dicoding.tvapp.ui.list.ShowListScreen

sealed class Screen(val route: String) {
    object List : Screen("list")
    object Detail : Screen("detail/{showId}") {
        fun createRoute(showId: Int) = "detail/$showId"
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.List.route
    ) {
        composable(Screen.List.route) {
            ShowListScreen(
                onShowClick = { showId ->
                    navController.navigate(Screen.Detail.createRoute(showId))
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("showId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val showId = backStackEntry.arguments?.getInt("showId") ?: return@composable
            ShowDetailScreen(
                showId = showId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
