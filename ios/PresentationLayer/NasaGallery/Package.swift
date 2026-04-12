// swift-tools-version: 5.10
import PackageDescription

let package = Package(
    name: "NasaGallery",
    platforms: [.iOS(.v16)],
    products: [
        // Module name must differ from the iOS app target (`NasaGallery`) or Xcode emits duplicate swiftmodule outputs.
        .library(name: "NasaGalleryRoot", targets: ["NasaGalleryRoot"])
    ],
    dependencies: [
        .package(name: "UIToolkit", path: "../UIToolkit"),
        .package(name: "DependencyInjection", path: "../../Application/DependencyInjection"),
        .package(name: "AuthFeature", path: "../AuthFeature"),
        .package(name: "DashboardFeature", path: "../DashboardFeature"),
        .package(name: "FavoritesFeature", path: "../FavoritesFeature"),
        .package(name: "SettingsFeature", path: "../SettingsFeature")
    ],
    targets: [
        .target(
            name: "NasaGalleryRoot",
            dependencies: [
                .product(name: "UIToolkit", package: "UIToolkit"),
                .product(name: "DependencyInjection", package: "DependencyInjection"),
                .product(name: "AuthFeature", package: "AuthFeature"),
                .product(name: "DashboardFeature", package: "DashboardFeature"),
                .product(name: "FavoritesFeature", package: "FavoritesFeature"),
                .product(name: "SettingsFeature", package: "SettingsFeature")
            ],
            path: "Sources/NasaGalleryRoot"
        )
    ]
)
