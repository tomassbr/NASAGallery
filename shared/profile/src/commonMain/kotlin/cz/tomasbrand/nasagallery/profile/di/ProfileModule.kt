package cz.tomasbrand.nasagallery.profile.di

import cz.tomasbrand.nasagallery.profile.presentation.ProfileViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module {
    includes(profilePlatformModule)
    viewModelOf(::ProfileViewModel)
}

internal expect val profilePlatformModule: Module
