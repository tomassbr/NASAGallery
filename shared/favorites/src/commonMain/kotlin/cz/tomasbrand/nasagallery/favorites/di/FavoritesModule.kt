package cz.tomasbrand.nasagallery.favorites.di

import cz.tomasbrand.nasagallery.favorites.data.local.FavoritesSource
import cz.tomasbrand.nasagallery.favorites.presentation.FavoritesViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val favoritesModule = module {
    singleOf(::FavoritesSource)
    viewModelOf(::FavoritesViewModel)
}
