import java.util.Properties

plugins {
    alias(libs.plugins.nasaGallery.kmp.library.core)
    alias(libs.plugins.serialization)
}

android {
    namespace = "cz.tomasbrand.nasagallery.shared.network"

    val localProps = project.rootProject.file("local.properties")
    val nasaApiKey: String = if (localProps.exists()) {
        val props = Properties()
        props.load(localProps.inputStream())
        props.getProperty("nasa.api.key", "DEMO_KEY")
    } else {
        "DEMO_KEY"
    }

    defaultConfig {
        buildConfigField("String", "NASA_API_KEY", "\"$nasaApiKey\"")
    }
    buildFeatures {
        buildConfig = true
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:base"))
            implementation(libs.bundles.ktor.common)
            implementation(libs.kermit)
        }
        androidMain.dependencies {
            implementation(libs.ktor.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.ios)
        }
    }
}
