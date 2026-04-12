plugins {
    alias(libs.plugins.nasaGallery.android.library.compose)
}

android {
    namespace = "kmp.android.shared"
}

dependencies {
    implementation(project(":shared:umbrella"))
    implementation(libs.googlePlayServices.location)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
}
