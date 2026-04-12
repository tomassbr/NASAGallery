package kmp.android.apod.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import kmp.android.shared.navigation.Destination
import kmp.android.shared.navigation.FeatureGraph

object ApodGraph : FeatureGraph(parent = null) {
    override val path = "home_tab"

    object Home : Destination(ApodGraph) {
        override val routeDefinition = "home"
    }

    /** Full gallery + search (opened from Home “See all”). */
    object Explore : Destination(ApodGraph) {
        override val routeDefinition = "explore"
    }

    object MediaDetail : Destination(ApodGraph) {
        override val routeDefinition = "detail"
        const val ARG_NASA_ID = "nasaId"
        override val arguments = listOf(
            navArgument(ARG_NASA_ID) { type = NavType.StringType },
        )
    }
}
