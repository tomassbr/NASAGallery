package kmp.android.apod.navigation

import kmp.android.shared.navigation.Destination
import kmp.android.shared.navigation.FeatureGraph

object ApodGraph : FeatureGraph(parent = null) {
    override val path = "home_tab"

    object Home : Destination(ApodGraph) {
        override val routeDefinition = "home"
    }
}
