import co.touchlab.skie.configuration.DefaultArgumentInterop

plugins {
    alias(libs.plugins.nasaGallery.kmp.framework.library)
    alias(libs.plugins.skie)
}

skie {
    swiftBundling {
        enabled = true
    }

    features {
        group {
            DefaultArgumentInterop.Enabled(false)
        }
    }
}

android {
    namespace = "cz.tomasbrand.nasagallery.shared.umbrella"
}

multiplatformResources {
    resourcesPackage.set("cz.tomasbrand.nasagallery.shared.umbrella")
}

kotlin {
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.withType<org.jetbrains.kotlin.gradle.plugin.mpp.Framework> {
            export(project(":shared:auth"))
            export(project(":shared:apod"))
            export(project(":shared:gallery"))
            export(project(":shared:search"))
            export(project(":shared:favorites"))
            export(project(":shared:profile"))
            export(project(":shared:network"))
        }
    }
}

dependencies {
    commonMainApi(project(":shared:base"))
    commonMainApi(project(":shared:analytics"))
    commonMainApi(project(":shared:samplefeature"))
    commonMainApi(project(":shared:auth"))
    commonMainApi(project(":shared:network"))
    commonMainApi(project(":shared:database"))
    commonMainApi(project(":shared:apod"))
    commonMainApi(project(":shared:gallery"))
    commonMainApi(project(":shared:search"))
    commonMainApi(project(":shared:favorites"))
    commonMainApi(project(":shared:profile"))
}
