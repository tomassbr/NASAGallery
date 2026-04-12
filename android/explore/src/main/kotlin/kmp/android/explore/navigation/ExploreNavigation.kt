package kmp.android.explore.navigation

// Standalone explore_tab graph (optional entry points / deep links). Primary flow is under home_tab via ApodGraph.
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import kmp.android.explore.ui.ExploreRoute
import kmp.android.explore.ui.MediaDetailRoute
import kmp.android.shared.navigation.composableDestination

fun NavGraphBuilder.exploreNavGraph(navController: NavHostController) {
    navigation(
        startDestination = ExploreGraph.Main.route,
        route = ExploreGraph.rootPath,
    ) {
        composableDestination(ExploreGraph.Main) {
            ExploreRoute(
                onNavigateToDetail = { nasaId -> navController.navigate(ExploreGraph.Detail(nasaId)) },
            )
        }
        composableDestination(ExploreGraph.Detail) { backStackEntry ->
            val nasaId = backStackEntry.arguments?.getString(ExploreGraph.Detail.ARG_NASA_ID) ?: return@composableDestination
            MediaDetailRoute(nasaId = nasaId, navController = navController)
        }
    }
}
