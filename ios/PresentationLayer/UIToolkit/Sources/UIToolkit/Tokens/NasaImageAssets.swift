import SwiftUI

// MARK: - NASA image assets (Images.xcassets)
// Template PDFs — use with `.renderingMode(.template)` when applying gradients or solid fills.

public enum NasaImageAsset {

    public enum TabBar {
        public static let home = Image("ic-home", bundle: UIToolkitResourceBundle.value)
        public static let heart = Image("ic-heart", bundle: UIToolkitResourceBundle.value)
        public static let settings = Image("ic-settings", bundle: UIToolkitResourceBundle.value)
        public static let user = Image("ic-user", bundle: UIToolkitResourceBundle.value)
    }

    public enum Icon {
        public static let search = Image("ic-search", bundle: UIToolkitResourceBundle.value)
        public static let share = Image("ic-share", bundle: UIToolkitResourceBundle.value)
        public static let trash = Image("ic-trash", bundle: UIToolkitResourceBundle.value)
        public static let download = Image("ic-download", bundle: UIToolkitResourceBundle.value)
        public static let fullscreen = Image("ic-fullscreen", bundle: UIToolkitResourceBundle.value)
        public static let lostSignal = Image("ic-lost-signal", bundle: UIToolkitResourceBundle.value)
        public static let link = Image("ic-link", bundle: UIToolkitResourceBundle.value)
        public static let album = Image("ic-album", bundle: UIToolkitResourceBundle.value)
        public static let server = Image("ic-server", bundle: UIToolkitResourceBundle.value)
        public static let sort = Image("ic-sort", bundle: UIToolkitResourceBundle.value)
        public static let theme = Image("ic-theme", bundle: UIToolkitResourceBundle.value)
        public static let arrowLeft = Image("ic-arrow-left", bundle: UIToolkitResourceBundle.value)
        public static let emptySearch = Image("ic-empty-search", bundle: UIToolkitResourceBundle.value)
    }
}
