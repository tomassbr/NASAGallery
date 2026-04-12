plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.ktlint)
}

group = "buildlogic"

kotlin {
    val versionCode = libs.versions.java.get().toInt()
    jvmToolchain(versionCode)
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    compileOnly(libs.androidTools.gradle)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ktlint.gradlePlugin)
    compileOnly(libs.sentiary.gradlePlugin)
}

gradlePlugin {
    plugins {
        plugin(
            dependency = libs.plugins.nasaGallery.android.application.compose,
            pluginName = "AndroidApplicationComposeConventionPlugin",
        )
        plugin(
            dependency = libs.plugins.nasaGallery.android.application.core,
            pluginName = "AndroidApplicationConventionPlugin",
        )
        plugin(
            dependency = libs.plugins.nasaGallery.android.library.compose,
            pluginName = "AndroidLibraryComposeConventionPlugin",
        )
        plugin(
            dependency = libs.plugins.nasaGallery.android.library.core,
            pluginName = "AndroidLibraryConventionPlugin",
        )
        plugin(
            dependency = libs.plugins.nasaGallery.kmp.library.core,
            pluginName = "KmpLibraryConventionPlugin",
        )
        plugin(
            dependency = libs.plugins.nasaGallery.kmp.library.compose,
            pluginName = "KmpLibraryComposeConventionPlugin",
        )
        plugin(
            dependency = libs.plugins.nasaGallery.kmp.framework.library,
            pluginName = "KmpFrameworkLibraryConventionPlugin",
        )
        plugin(
            dependency = libs.plugins.nasaGallery.kmp.sentiary,
            pluginName = "SentiaryConvention",
        )
    }
}

fun NamedDomainObjectContainer<PluginDeclaration>.plugin(
    dependency: Provider<out PluginDependency>,
    pluginName: String,
) {
    val pluginId = dependency.get().pluginId
    register(pluginId) {
        id = pluginId
        implementationClass = "plugin.$pluginName"
    }
}
