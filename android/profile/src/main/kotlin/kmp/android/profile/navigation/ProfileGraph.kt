package kmp.android.profile.navigation

import kmp.android.shared.navigation.Destination
import kmp.android.shared.navigation.FeatureGraph

object ProfileGraph : FeatureGraph(parent = null) {
    override val path = "profile_tab"

    object Main : Destination(ProfileGraph) {
        override val routeDefinition = "profile"
    }
}
