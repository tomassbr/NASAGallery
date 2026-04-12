package kmp.android.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import kmp.android.profile.ui.ProfileRoute
import kmp.android.shared.navigation.composableDestination

fun NavGraphBuilder.profileNavGraph(navController: NavHostController) {
    navigation(
        startDestination = ProfileGraph.Main.route,
        route = ProfileGraph.rootPath,
    ) {
        composableDestination(ProfileGraph.Main) {
            ProfileRoute()
        }
    }
}
