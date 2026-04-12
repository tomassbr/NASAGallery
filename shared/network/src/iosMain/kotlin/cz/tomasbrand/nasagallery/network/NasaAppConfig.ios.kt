package cz.tomasbrand.nasagallery.network

import platform.Foundation.NSBundle

actual class NasaAppConfig actual constructor() {
    // NASA_API_KEY must be set in Info.plist as NASA_API_KEY string via xcconfig
    actual val nasaApiKey: String =
        NSBundle.mainBundle.objectForInfoDictionaryKey("NASA_API_KEY") as? String
            ?: "DEMO_KEY"
}
