import Foundation

/// Bundle that contains `Colors.xcassets` and `Images.xcassets` from UIToolkit.
/// Use this when loading `Image(_:bundle:)` / `Color(_:bundle:)` from feature modules outside UIToolkit.
public enum UIToolkitResourceBundle {
    public static let value: Bundle = {
        #if SWIFT_PACKAGE
        return Bundle.module
        #else
        return Bundle(for: UIToolkitBundleLocator.self)
        #endif
    }()
}

private final class UIToolkitBundleLocator {}
