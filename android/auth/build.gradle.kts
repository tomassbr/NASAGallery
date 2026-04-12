plugins {
    alias(libs.plugins.nasaGallery.android.library.compose)
}

android {
    namespace = "kmp.android.auth"
}

dependencies {
    implementation(project(":android:shared"))
    implementation(project(":shared:umbrella"))
}
