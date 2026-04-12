plugins {
    alias(libs.plugins.nasaGallery.kmp.library.compose)
}

android {
    namespace = "cz.tomasbrand.nasagallery.shared.favorites"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:auth"))
            implementation(project(":shared:base"))
            implementation(project(":shared:gallery"))
            implementation(project(":shared:database"))
            implementation(libs.coroutines.core)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.kotlinx.immutableCollections)
        }
    }
}
