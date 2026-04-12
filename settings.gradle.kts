pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven("https://plugins.gradle.org/m2/")
    }
}

rootProject.buildFileName = "build.gradle.kts"
rootProject.name = "NasaGallery"

include(":android:app")
include(":android:shared")
include(":android:auth")
include(":android:apod")
include(":android:explore")
include(":android:saved")
include(":android:profile")

include(":shared:samplefeature")
include(":shared:umbrella")
include(":shared:base")
include(":shared:analytics")
include(":shared:auth")
include(":shared:network")
include(":shared:database")
include(":shared:apod")
include(":shared:gallery")
include(":shared:search")
include(":shared:favorites")
include(":shared:profile")
