plugins {
    alias(libs.plugins.nasaGallery.kmp.library.compose)
}

android {
    namespace = "cz.tomasbrand.nasagallery.shared.apod"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:base"))
            implementation(project(":shared:network"))
            implementation(project(":shared:database"))
            implementation(libs.coroutines.core)
            implementation(libs.dateTime)
        }
    }
}
