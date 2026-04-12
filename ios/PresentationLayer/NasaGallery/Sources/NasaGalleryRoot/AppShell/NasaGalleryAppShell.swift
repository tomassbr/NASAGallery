import SwiftUI
import UIKit
import UIToolkit
import Utilities

/// Application-wide setup kept in this module so the app target only links `NasaGalleryRoot` + `Atlantis`
/// (avoids duplicate static SPM links that can break Swift opaque-type metadata at link time).
public enum NasaGalleryAppShell {

    public static func configureEnvironmentForLaunch() {
        #if ALPHA
        Environment.api = .alpha
        Logger.app.info("ALPHA environment")
        #elseif PRODUCTION
        Environment.api = .production
        Logger.app.info("PRODUCTION environment")
        #endif

        #if DEBUG
        Environment.build = .debug
        #else
        Environment.build = .release
        #endif
    }

    public static func configureFirebaseDebugArgumentsIfNeeded() {
        if Environment.api != .production {
            var args = ProcessInfo.processInfo.arguments
            args.append("-FIRAnalyticsDebugEnabled")
            ProcessInfo.processInfo.setValue(args, forKey: "arguments")
        }
    }

    public static func configureURLCache() {
        URLCache.shared.memoryCapacity = 10_000_000
        URLCache.shared.diskCapacity = 1_000_000_000
    }

    public static func configureUIKitAppearances() {
        let navAppearance = UINavigationBarAppearance()
        navAppearance.configureWithOpaqueBackground()
        navAppearance.backgroundColor = UIColor(Color.nasaBackground)
        navAppearance.titleTextAttributes = [.foregroundColor: UIColor(Color.nasaOnBackground)]
        navAppearance.largeTitleTextAttributes = [.foregroundColor: UIColor(Color.nasaOnBackground)]
        UINavigationBar.appearance().standardAppearance = navAppearance
        UINavigationBar.appearance().scrollEdgeAppearance = navAppearance
        UINavigationBar.appearance().tintColor = UIColor(Color.nasaPrimary)

        let tabAppearance = UITabBarAppearance()
        tabAppearance.configureWithOpaqueBackground()
        tabAppearance.backgroundColor = UIColor(Color.nasaBackground)
        UITabBar.appearance().standardAppearance = tabAppearance
        UITabBar.appearance().scrollEdgeAppearance = tabAppearance
        UITabBar.appearance().tintColor = UIColor(Color.nasaPrimary)
        UITabBar.appearance().unselectedItemTintColor = UIColor(Color.nasaSubtle)

        UITextField.appearance().tintColor = UIColor(Color.nasaPrimary)
    }
}
