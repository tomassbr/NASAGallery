package cz.tomasbrand.nasagallery.network.client

import cz.tomasbrand.nasagallery.network.NasaApiConstants
import cz.tomasbrand.nasagallery.network.NasaApiKeyResolver
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import co.touchlab.kermit.Logger as KermitLogger

private val apiKeyQueryRedactor = Regex("""(?i)([?&]api_key=)[^&\s"]+""")

internal fun redactNasaHttpLogMessage(message: String): String =
    apiKeyQueryRedactor.replace(message) { "${it.groupValues[1]}***" }

private fun ktorDebugLogger(tag: String, redactApiKeyInQuery: Boolean): Logger =
    object : Logger {
        override fun log(message: String) {
            val line = if (redactApiKeyInQuery) redactNasaHttpLogMessage(message) else message
            KermitLogger.d { "[$tag] $line" }
        }
    }

internal val nasaJson = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    useAlternativeNames = false
}

/**
 * Ktor client for api.nasa.gov — requires NASA API key injected as query param.
 * Used for APOD endpoint.
 */
internal fun buildApodClient(
    engine: HttpClientEngine,
    apiKeyResolver: NasaApiKeyResolver,
    isDebug: Boolean,
): HttpClient = HttpClient(engine) {
    expectSuccess = true

    install(ContentNegotiation) {
        json(nasaJson)
    }

    if (isDebug) {
        install(Logging) {
            logger = ktorDebugLogger(tag = "APOD", redactApiKeyInQuery = true)
            level = LogLevel.ALL
        }
    }

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = NasaApiConstants.APOD_BASE_URL
            // Append api_key to every request
            parameters.append(NasaApiConstants.QueryParams.API_KEY, apiKeyResolver())
        }
    }
}

/**
 * Ktor client for images-api.nasa.gov — no API key required.
 * Used for image search, asset retrieval, and metadata endpoints.
 */
internal fun buildImagesClient(
    engine: HttpClientEngine,
    isDebug: Boolean,
): HttpClient = HttpClient(engine) {
    expectSuccess = true

    install(ContentNegotiation) {
        json(nasaJson)
    }

    if (isDebug) {
        install(Logging) {
            logger = ktorDebugLogger(tag = "Images", redactApiKeyInQuery = false)
            level = LogLevel.ALL
        }
    }

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = NasaApiConstants.IMAGES_BASE_URL
        }
    }
}
