#if ALPHA
import Atlantis
#endif

import NasaGalleryRoot
import SwiftUI
import UIKit

final class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {

        #if ALPHA
        Atlantis.start()
        #endif

        NasaGalleryAppShell.configureEnvironmentForLaunch()
        NasaGalleryAppShell.configureFirebaseDebugArgumentsIfNeeded()
        NasaGalleryAppShell.configureURLCache()

        application.registerForRemoteNotifications()

        NasaGalleryAppShell.configureUIKitAppearances()

        return true
    }
}
