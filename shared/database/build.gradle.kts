plugins {
    alias(libs.plugins.nasaGallery.kmp.library.core)
    alias(libs.plugins.sqldelight)
}

android {
    namespace = "cz.tomasbrand.nasagallery.shared.database"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:base"))
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqldelight.primitive.adapters)
            implementation(libs.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.sqldelight.android.driver)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
    }
}

sqldelight {
    databases {
        create("NasaDatabase") {
            packageName.set("cz.tomasbrand.nasagallery.database")
        }
    }
}
