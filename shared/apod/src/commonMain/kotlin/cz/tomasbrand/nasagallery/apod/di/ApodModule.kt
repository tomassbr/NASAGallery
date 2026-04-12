package cz.tomasbrand.nasagallery.apod.di

import cz.tomasbrand.nasagallery.apod.data.local.ApodCacheSource
import cz.tomasbrand.nasagallery.apod.data.remote.ApodService
import cz.tomasbrand.nasagallery.apod.data.repository.ApodRepository
import cz.tomasbrand.nasagallery.apod.data.repository.ApodRepositoryImpl
import cz.tomasbrand.nasagallery.apod.domain.usecase.GetApodForDateUseCase
import cz.tomasbrand.nasagallery.apod.domain.usecase.GetApodForDateUseCaseImpl
import cz.tomasbrand.nasagallery.apod.domain.usecase.GetTodayApodUseCase
import cz.tomasbrand.nasagallery.apod.domain.usecase.GetTodayApodUseCaseImpl
import cz.tomasbrand.nasagallery.apod.presentation.ApodViewModel
import cz.tomasbrand.nasagallery.network.di.NasaClientNames
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val apodModule = module {
    single { ApodService(get<HttpClient>(named(NasaClientNames.APOD))) }
    singleOf(::ApodCacheSource)
    singleOf(::ApodRepositoryImpl) bind ApodRepository::class
    singleOf(::GetTodayApodUseCaseImpl) bind GetTodayApodUseCase::class
    singleOf(::GetApodForDateUseCaseImpl) bind GetApodForDateUseCase::class
    viewModelOf(::ApodViewModel)
}
