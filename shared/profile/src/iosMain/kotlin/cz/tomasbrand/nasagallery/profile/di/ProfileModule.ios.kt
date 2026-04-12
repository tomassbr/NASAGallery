package cz.tomasbrand.nasagallery.profile.di

import cz.tomasbrand.nasagallery.profile.device.IosImageCacheClearer
import cz.tomasbrand.nasagallery.profile.domain.ImageCacheClearer
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val profilePlatformModule: Module = module {
    single<ImageCacheClearer> { IosImageCacheClearer() }
}
