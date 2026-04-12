package cz.tomasbrand.nasagallery.network

/**
 * Resolves the NASA API key for each APOD request so a key edited in Settings overrides [NasaAppConfig].
 */
fun interface NasaApiKeyResolver {
    operator fun invoke(): String
}
