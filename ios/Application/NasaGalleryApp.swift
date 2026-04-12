//  NasaGalleryApp.swift
//  NASA Gallery

import NasaGalleryRoot
import SwiftUI

@main
struct NasaGalleryApp: App {

    @Environment(\.scenePhase) private var scenePhase

    @UIApplicationDelegateAdaptor private var appDelegate: AppDelegate

    var body: some Scene {
        WindowGroup {
            RootCoordinator()
        }
    }
}
