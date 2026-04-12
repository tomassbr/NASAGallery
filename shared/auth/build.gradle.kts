plugins {
    alias(libs.plugins.nasaGallery.kmp.library.compose)
}

android {
    namespace = "kmp.shared.auth"
}

dependencies {
    commonMainImplementation(project(":shared:base"))
}
