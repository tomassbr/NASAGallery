package kmp.android.saved.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import kmp.android.saved.ui.SavedRoute
import kmp.android.shared.navigation.composableDestination

fun NavGraphBuilder.savedNavGraph(navController: NavHostController) {
    navigation(
        startDestination = SavedGraph.Main.route,
        route = SavedGraph.rootPath,
    ) {
        composableDestination(SavedGraph.Main) {
            SavedRoute(navController = navController)
        }
    }
}
