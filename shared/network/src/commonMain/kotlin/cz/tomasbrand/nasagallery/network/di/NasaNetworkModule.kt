package cz.tomasbrand.nasagallery.network.di

import cz.tomasbrand.nasagallery.network.NasaAppConfig
import cz.tomasbrand.nasagallery.network.client.buildApodClient
import cz.tomasbrand.nasagallery.network.client.buildImagesClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kmp.shared.base.domain.system.Config
import org.koin.core.qualifier.named
import org.koin.dsl.module

val nasaNetworkModule = module {
    single { NasaAppConfig() }

    single(named(NasaClientNames.APOD)) {
        buildApodClient(
            engine = get<HttpClientEngine>(),
            config = get(),
            isDebug = !get<Config>().isRelease,
        )
    }

    single(named(NasaClientNames.IMAGES)) {
        buildImagesClient(
            engine = get<HttpClientEngine>(),
            isDebug = !get<Config>().isRelease,
        )
    }
}

object NasaClientNames {
    const val APOD = "nasa_apod_client"
    const val IMAGES = "nasa_images_client"
}

/** Convenience extension to retrieve the APOD Ktor client from Koin. */
fun getApodHttpClient(client: HttpClient) = client
