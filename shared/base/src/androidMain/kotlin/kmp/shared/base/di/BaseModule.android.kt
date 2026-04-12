package kmp.shared.base.di

import com.russhwolf.settings.Settings
import io.ktor.client.engine.android.Android
import kmp.shared.base.data.preferences.NasaApiKeyStore
import kmp.shared.base.data.preferences.SharedPreferencesFactory
import kmp.shared.base.data.preferences.SharedPreferencesType
import kmp.shared.base.domain.system.Config
import kmp.shared.base.domain.system.ConfigImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val basePlatformModule = module {
    singleOf(::ConfigImpl) bind Config::class
    single { Android.create() }

    factoryOf(::SharedPreferencesFactory)

    single {
        NasaApiKeyStore(
            secureSettings = get<SharedPreferencesFactory>().create(
                fileName = "nasa_api_key_store",
                type = SharedPreferencesType.ENCRYPTED,
            ),
            legacySettings = get<Settings>(),
        )
    }
}
