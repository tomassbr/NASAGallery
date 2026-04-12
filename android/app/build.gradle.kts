plugins {
    alias(libs.plugins.nasaGallery.android.application.compose)
    alias(libs.plugins.google.services)
}

android {
    namespace = "cz.tomasbrand.nasagallery.android"
}

dependencies {
    implementation(project(":shared:umbrella"))
    implementation(project(":android:shared"))
    implementation(project(":android:auth"))
    implementation(project(":android:apod"))
    implementation(project(":android:explore"))
    implementation(project(":android:saved"))
    implementation(project(":android:profile"))
}
