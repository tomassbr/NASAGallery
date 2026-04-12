plugins {
    alias(libs.plugins.nasaGallery.kmp.library.compose)
}

android {
    namespace = "cz.tomasbrand.nasagallery.shared.profile"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:base"))
            implementation(libs.bundles.settings)
        }
        androidMain.dependencies {
            implementation(libs.coil.compose)
        }
    }
}
