import SwiftUI

// MARK: - NASA Spacing Tokens
// Use these constants everywhere instead of hardcoded numbers.
// Grid-based: base unit = 4pt
public extension CGFloat {
    static let spaceXXS: CGFloat = 2
    static let spaceXS: CGFloat  = 4
    static let spaceSM: CGFloat  = 8
    /// Figma variable `space/12` — filter chip row gap, tight stacks
    static let space12: CGFloat  = 12
    static let spaceMD: CGFloat  = 16
    static let spaceLG: CGFloat  = 24
    static let spaceXL: CGFloat  = 32
    static let spaceXXL: CGFloat = 48
    static let spaceXXXL: CGFloat = 64

    // Semantic spacing
    static let screenHorizontalPadding: CGFloat = .spaceMD
    static let cardPadding: CGFloat             = .spaceMD
    static let sectionSpacing: CGFloat          = .spaceXL
    static let itemSpacing: CGFloat             = .spaceSM
    static let iconSize: CGFloat                = 24
    static let iconSizeLG: CGFloat              = 32
    static let tabBarHeight: CGFloat            = 49
    /// Custom bottom tab chrome (`MainTabView` / `NasaTabBar`) above the home indicator.
    static let tabBarChromeHeight: CGFloat      = 56
    static let navigationBarHeight: CGFloat     = 44
}

// MARK: - SwiftUI convenience
public extension EdgeInsets {
    static let screenPadding = EdgeInsets(
        top: .spaceMD,
        leading: .screenHorizontalPadding,
        bottom: .spaceMD,
        trailing: .screenHorizontalPadding
    )
}
