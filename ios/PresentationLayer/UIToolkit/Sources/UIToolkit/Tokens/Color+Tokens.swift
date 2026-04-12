import SwiftUI

// MARK: - NASA Color Tokens
// Programmatic sRGB values mirror the **first** (default / universal) slot in
// `Resources/Colors.xcassets`. Asset-backed `Color(_:bundle:)` is unreliable for
// SPM-processed catalogs (names like `Surface/Level1` often do not resolve in
// `UIToolkit_UIToolkit.bundle` at runtime), so tokens use `Color(hex:opacity:)`.
// Figma reference: file 32OyUPY6rvNLgEpVgKWV92, Style Guide node 8:677

public extension Color {

    // MARK: Backgrounds
    /// bg/canvas — main screen backgrounds (`Background/Canvas`)
    static let nasaBackground = Color(hex: 0x0B0D14)
    /// bg/base — deepest layer (`Background/Base`)
    static let nasaBackgroundSecondary = Color(hex: 0x050608)

    // MARK: Surfaces
    /// Cards, sheets (`Surface/Level1`)
    static let nasaSurface = Color(hex: 0x12151F)
    /// Skeleton, elevated (`Surface/Level2`)
    static let nasaSurfaceElevated = Color(hex: 0x161925)
    /// Glass tint (`Surface/GlassTint`)
    static let nasaSurfaceGlassTint = Color(red: 11 / 255, green: 13 / 255, blue: 20 / 255, opacity: 0.72)

    // MARK: Content
    /// text/primary — main labels on background / surface (`Text/Primary`)
    static let nasaOnBackground = Color(hex: 0xF1F5F9)
    /// text/primary
    static let nasaOnSurface = Color(hex: 0xF1F5F9)
    /// text/secondary (`Text/Secondary`)
    static let nasaOnSurfaceVariant = Color(hex: 0xCBD5E1)
    /// text/muted — secondary nav, inactive tab icons (`Text/Muted`)
    static let nasaSubtle = Color(hex: 0x64748B)
    /// Chip / filter label resting — aligned with secondary text (`Text/Secondary`)
    static let nasaChipLabelResting = Color(hex: 0xCBD5E1)

    // MARK: Brand
    /// accent/primary — selected states, highlights (`Accent/Primary`)
    static let nasaPrimary = Color(hex: 0x818CF8)
    /// Text / icons on primary-colored surfaces (`Text/OnAccent`)
    static let nasaOnPrimary = Color(hex: 0x050608)
    /// accent/secondary — links, secondary CTA (`Accent/Secondary`)
    static let nasaAccent = Color(hex: 0x2DD4BF)

    // MARK: Semantic
    static let nasaSuccess = Color(hex: 0x22C55E)
    static let nasaError = Color(hex: 0xFB7185)
    static let nasaWarning = Color(hex: 0xF59E0B)
    /// Info / inline badges (`Status/Info`)
    static let nasaInfo = Color(hex: 0x38BDF8)

    // MARK: Borders
    static let nasaBorderSubtle = Color(hex: 0xFEFFFF, opacity: 0.08)
    static let nasaBorderStrong = Color(hex: 0xFEFFFF, opacity: 0.14)
    static let nasaBorderGlassHighlight = Color(hex: 0xFEFFFF, opacity: 0.10)

    // MARK: Overlays (not in catalog — keep programmatic)
    static let nasaMediaOverlay = Color.black.opacity(0.45)
}

// MARK: - Hex initializer (ad-hoc colors, previews, tests)
public extension Color {
    init(hex: UInt32, opacity: Double = 1) {
        let r = Double((hex >> 16) & 0xff) / 255
        let g = Double((hex >> 8) & 0xff) / 255
        let b = Double(hex & 0xff) / 255
        self.init(red: r, green: g, blue: b, opacity: opacity)
    }
}
