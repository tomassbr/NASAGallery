import SwiftUI
import UIToolkit

// MARK: - NasaTab
// 3-tab navigation: Home / Favorites / Settings (content: SettingsFeature).
public enum NasaTab: Int, CaseIterable, Hashable {
    case home
    case favorites
    case settings

    public var title: String {
        switch self {
        case .home: return "Home"
        case .favorites: return "Favorites"
        case .settings: return "Settings"
        }
    }

    /// Tab bar icons from `UIToolkit` `Images.xcassets` (template PDFs).
    public var tabBarIcon: Image {
        switch self {
        case .home: return NasaImageAsset.TabBar.home
        case .favorites: return NasaImageAsset.TabBar.heart
        case .settings: return NasaImageAsset.TabBar.settings
        }
    }
}
