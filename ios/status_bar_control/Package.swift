// swift-tools-version: 5.9
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "status_bar_control",
    platforms: [
        .iOS("13.0")
    ],
    products: [
        .library(name: "status-bar-control", targets: ["status_bar_control"])
    ],
    dependencies: [
        .package(name: "FlutterFramework", path: "../FlutterFramework")
    ],
    targets: [
        .target(
            name: "status_bar_control",
            dependencies: [
                .product(name: "FlutterFramework", package: "FlutterFramework")
            ],
            cSettings: [
                .headerSearchPath("include/status_bar_control")
            ]
        )
    ]
)
