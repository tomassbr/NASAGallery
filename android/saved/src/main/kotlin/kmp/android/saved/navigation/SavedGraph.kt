package kmp.android.saved.navigation

import kmp.android.shared.navigation.Destination
import kmp.android.shared.navigation.FeatureGraph

object SavedGraph : FeatureGraph(parent = null) {
    override val path = "favorites_tab"

    object Main : Destination(SavedGraph) {
        override val routeDefinition = "favorites"
    }
}
