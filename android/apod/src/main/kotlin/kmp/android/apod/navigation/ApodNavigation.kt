package kmp.android.apod.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import kmp.android.apod.ui.HomeRoute
import kmp.android.explore.ui.ExploreRoute
import kmp.android.explore.ui.MediaDetailRoute
import kmp.android.shared.navigation.composableDestination

fun NavGraphBuilder.apodNavGraph(navController: NavHostController) {
    navigation(
        startDestination = ApodGraph.Home.route,
        route = ApodGraph.rootPath,
    ) {
        composableDestination(ApodGraph.Home) {
            HomeRoute(navController = navController)
        }
        composableDestination(ApodGraph.Explore) {
            ExploreRoute(
                onNavigateToDetail = { nasaId ->
                    navController.navigate(ApodGraph.MediaDetail(nasaId))
                },
            )
        }
        composableDestination(ApodGraph.MediaDetail) { backStackEntry ->
            val nasaId =
                backStackEntry.arguments?.getString(ApodGraph.MediaDetail.ARG_NASA_ID) ?: return@composableDestination
            MediaDetailRoute(nasaId = nasaId, navController = navController)
        }
    }
}
