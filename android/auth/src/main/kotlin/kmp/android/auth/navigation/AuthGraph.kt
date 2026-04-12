package kmp.android.auth.navigation

import kmp.android.shared.navigation.Destination
import kmp.android.shared.navigation.FeatureGraph

object AuthGraph : FeatureGraph(parent = null) {
    override val path = "auth"

    object Main : Destination(AuthGraph) {
        override val routeDefinition = "main"
    }
}
