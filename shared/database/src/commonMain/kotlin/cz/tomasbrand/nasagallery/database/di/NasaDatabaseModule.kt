package cz.tomasbrand.nasagallery.database.di

import cz.tomasbrand.nasagallery.database.DatabaseDriverFactory
import cz.tomasbrand.nasagallery.database.NasaDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val nasaDatabaseModule = module {
    singleOf(::DatabaseDriverFactory)

    single {
        NasaDatabase(driver = get<DatabaseDriverFactory>().createDriver())
    }

    single { get<NasaDatabase>().favoriteQueries }

    single { get<NasaDatabase>().apodCacheQueries }
}
