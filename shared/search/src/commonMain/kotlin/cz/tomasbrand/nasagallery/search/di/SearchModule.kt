package cz.tomasbrand.nasagallery.search.di

import cz.tomasbrand.nasagallery.network.di.NasaClientNames
import cz.tomasbrand.nasagallery.search.data.remote.SearchService
import cz.tomasbrand.nasagallery.search.presentation.SearchViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val searchModule = module {
    single { SearchService(get<HttpClient>(named(NasaClientNames.IMAGES))) }
    viewModelOf(::SearchViewModel)
}
