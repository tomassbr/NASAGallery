package kmp.android.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import kmp.android.auth.ui.AuthRoute
import kmp.android.shared.navigation.composableDestination

fun NavGraphBuilder.authNavGraph(
    onNavigateToMain: () -> Unit,
    navHostController: NavHostController,
) {
    navigation(
        startDestination = AuthGraph.Main.route,
        route = AuthGraph.rootPath,
    ) {
        composableDestination(AuthGraph.Main) {
            AuthRoute(onNavigateToMain = onNavigateToMain)
        }
    }
}
