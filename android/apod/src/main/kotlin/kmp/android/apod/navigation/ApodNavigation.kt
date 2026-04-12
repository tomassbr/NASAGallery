package kmp.android.apod.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import kmp.android.apod.ui.HomeRoute
import kmp.android.shared.navigation.composableDestination

fun NavGraphBuilder.apodNavGraph(navController: NavHostController) {
    navigation(
        startDestination = ApodGraph.Home.route,
        route = ApodGraph.rootPath,
    ) {
        composableDestination(ApodGraph.Home) {
            HomeRoute(navController = navController)
        }
    }
}
