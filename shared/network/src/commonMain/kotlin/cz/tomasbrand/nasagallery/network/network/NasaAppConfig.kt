package cz.tomasbrand.nasagallery.network

/**
 * Platform-specific configuration providing the NASA API key.
 * Key is read from BuildConfig on Android and Info.plist on iOS.
 * Never hardcode the API key.
 */
expect class NasaAppConfig() {
    val nasaApiKey: String
}
