plugins {
    alias(libs.plugins.nasaGallery.kmp.library.compose)
}

android {
    namespace = "cz.tomasbrand.nasagallery.shared.gallery"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:base"))
            implementation(project(":shared:network"))
            implementation(libs.coroutines.core)
            implementation(libs.kotlinx.immutableCollections)
        }
    }
}
