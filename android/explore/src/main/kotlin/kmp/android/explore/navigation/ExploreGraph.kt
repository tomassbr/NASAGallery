package kmp.android.explore.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import kmp.android.shared.navigation.Destination
import kmp.android.shared.navigation.FeatureGraph

object ExploreGraph : FeatureGraph(parent = null) {
    override val path = "explore_tab"

    object Main : Destination(ExploreGraph) {
        override val routeDefinition = "main"
    }

    object Detail : Destination(ExploreGraph) {
        override val routeDefinition = "detail"
        const val ARG_NASA_ID = "nasaId"
        override val arguments = listOf(
            navArgument(ARG_NASA_ID) { type = NavType.StringType },
        )
    }
}
