package kmp.shared.base.di

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import io.ktor.client.engine.darwin.Darwin
import kmp.shared.base.data.keychain.KeychainFactory
import kmp.shared.base.data.preferences.NasaApiKeyStore
import kmp.shared.base.data.userdefaults.UserDefaultsFactory
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

@OptIn(ExperimentalSettingsImplementation::class)
internal actual val basePlatformModule = module {
    single { Darwin.create() }
    factoryOf(::KeychainFactory)
    factoryOf(::UserDefaultsFactory)

    single {
        NasaApiKeyStore(
            secureSettings = KeychainSettings("cz.tomasbrand.nasagallery.nasa_api_key"),
            legacySettings = get<Settings>(),
        )
    }
}
