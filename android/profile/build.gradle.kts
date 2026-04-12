plugins {
    alias(libs.plugins.nasaGallery.android.library.compose)
}

android {
    namespace = "kmp.android.profile"
}

dependencies {
    implementation(project(":android:shared"))
    implementation(project(":shared:umbrella"))
    implementation(libs.compose.materialIconsExtended)
}
