package cz.tomasbrand.nasagallery.profile.di

import cz.tomasbrand.nasagallery.profile.device.AndroidImageCacheClearer
import cz.tomasbrand.nasagallery.profile.domain.ImageCacheClearer
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext

internal actual val profilePlatformModule: Module = module {
    single<ImageCacheClearer> { AndroidImageCacheClearer(androidContext()) }
}
