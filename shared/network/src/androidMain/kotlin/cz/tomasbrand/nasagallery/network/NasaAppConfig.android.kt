package cz.tomasbrand.nasagallery.network

import cz.tomasbrand.nasagallery.shared.network.BuildConfig

actual class NasaAppConfig actual constructor() {
    // NASA_API_KEY must be defined in local.properties as: nasa.api.key=YOUR_KEY
    // and added to buildConfigField in the network module's build.gradle.kts
    actual val nasaApiKey: String = BuildConfig.NASA_API_KEY
}
