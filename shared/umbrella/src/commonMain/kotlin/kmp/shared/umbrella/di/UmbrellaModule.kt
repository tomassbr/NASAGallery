package kmp.shared.umbrella.di

import cz.tomasbrand.nasagallery.apod.di.apodModule
import cz.tomasbrand.nasagallery.database.di.nasaDatabaseModule
import cz.tomasbrand.nasagallery.favorites.di.favoritesModule
import cz.tomasbrand.nasagallery.profile.di.profileModule
import cz.tomasbrand.nasagallery.gallery.di.galleryModule
import cz.tomasbrand.nasagallery.network.di.nasaNetworkModule
import cz.tomasbrand.nasagallery.search.di.searchModule
import kmp.shared.analytics.di.analyticsModule
import kmp.shared.auth.di.authModule
import kmp.shared.base.di.baseModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication {
    val koinApplication = startKoin {
        appDeclaration()
        modules(
            baseModule,
            authModule,
            analyticsModule,
            nasaNetworkModule,
            nasaDatabaseModule,
            apodModule,
            galleryModule,
            searchModule,
            favoritesModule,
            profileModule,
        )
    }

    val koin = koinApplication.koin
    val doOnStartup = koin.getOrNull<() -> Unit>()
    doOnStartup?.invoke()

    return koinApplication
}
