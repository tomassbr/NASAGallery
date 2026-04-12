plugins {
    alias(libs.plugins.nasaGallery.android.library.compose)
}

android {
    namespace = "kmp.android.explore"
}

dependencies {
    implementation(project(":android:shared"))
    implementation(project(":shared:umbrella"))
}
