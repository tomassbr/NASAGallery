package cz.tomasbrand.nasagallery.profile.di

import com.russhwolf.settings.Settings
import cz.tomasbrand.nasagallery.profile.presentation.ProfileViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module {
    singleOf(::Settings)
    viewModelOf(::ProfileViewModel)
}
