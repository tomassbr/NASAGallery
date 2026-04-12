// swift-tools-version: 5.10
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "SettingsFeature",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "SettingsFeature",
            targets: ["SettingsFeature"]
        )
    ],
    dependencies: [
        .package(name: "UIToolkit", path: "../UIToolkit"),
        .package(name: "Utilities", path: "../../DomainLayer/Utilities"),
        .package(name: "SharedDomain", path: "../../DomainLayer/SharedDomain"),
        .package(name: "DependencyInjection", path: "../../Application/DependencyInjection"),
        .package(url: "https://github.com/hmlongco/Factory.git", .upToNextMajor(from: "2.3.0")),
        .package(url: "https://github.com/hmlongco/Navigator", .upToNextMajor(from: "1.3.1")),
        .package(url: "https://github.com/SFSafeSymbols/SFSafeSymbols.git", .upToNextMajor(from: "5.3.0"))
    ],
    targets: [
        .target(
            name: "SettingsFeature",
            dependencies: [
                .product(name: "UIToolkit", package: "UIToolkit"),
                .product(name: "Utilities", package: "Utilities"),
                .product(name: "SharedDomain", package: "SharedDomain"),
                .product(name: "DependencyInjection", package: "DependencyInjection"),
                .product(name: "Factory", package: "Factory"),
                .product(name: "NavigatorUI", package: "Navigator"),
                .product(name: "SFSafeSymbols", package: "SFSafeSymbols")
            ]
        ),
        .testTarget(
            name: "SettingsFeatureTests",
            dependencies: [
                "SettingsFeature",
                .product(name: "UIToolkit", package: "UIToolkit"),
                .product(name: "SharedDomain", package: "SharedDomain"),
                .product(name: "DependencyInjection", package: "DependencyInjection"),
                .product(name: "Factory", package: "Factory")
            ]
        )
    ]
)
