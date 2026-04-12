package cz.tomasbrand.nasagallery.gallery.di

import cz.tomasbrand.nasagallery.gallery.data.remote.GalleryService
import cz.tomasbrand.nasagallery.gallery.data.repository.GalleryRepository
import cz.tomasbrand.nasagallery.gallery.data.repository.GalleryRepositoryImpl
import cz.tomasbrand.nasagallery.gallery.presentation.GalleryViewModel
import cz.tomasbrand.nasagallery.network.di.NasaClientNames
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val galleryModule = module {
    single { GalleryService(get<HttpClient>(named(NasaClientNames.IMAGES))) }
    singleOf(::GalleryRepositoryImpl) bind GalleryRepository::class
    viewModelOf(::GalleryViewModel)
}
