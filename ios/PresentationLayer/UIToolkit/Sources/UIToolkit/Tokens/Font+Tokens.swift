import SwiftUI

// MARK: - NASA Typography Tokens
// Type scale based on NASA Gallery Figma design system.
// Figma specifies **Inter** for UI and **Kode Mono** (monospace typeface) for code / mono text.
// (“Kode Mono” is unrelated to **Moko Resources** in KMP.) Those font files are not bundled yet;
// all tokens below use the system font. After adding `Inter-*.ttf` / `KodeMono-*.ttf` to the app
// target + `UIAppFonts`, replace with
// `Font.custom(_:size:)` (or a small factory) using the PostScript names from the font files.
public extension Font {
    // Display — hero titles, large headlines
    static let nasaDisplayLarge  = Font.system(size: 34, weight: .bold, design: .default)
    static let nasaDisplayMedium = Font.system(size: 28, weight: .semibold, design: .default)

    // Headline — screen titles, section headers
    static let nasaHeadlineLarge  = Font.system(size: 22, weight: .semibold, design: .default)
    static let nasaHeadlineMedium = Font.system(size: 18, weight: .semibold, design: .default)
    static let nasaHeadlineSmall  = Font.system(size: 16, weight: .medium, design: .default)

    // Title — card titles, list items
    static let nasaTitleLarge  = Font.system(size: 17, weight: .medium, design: .default)
    static let nasaTitleMedium = Font.system(size: 15, weight: .medium, design: .default)
    static let nasaTitleSmall  = Font.system(size: 14, weight: .medium, design: .default)

    // Body — descriptions, content text
    static let nasaBodyLarge  = Font.system(size: 17, weight: .regular, design: .default)
    static let nasaBodyMedium = Font.system(size: 15, weight: .regular, design: .default)
    static let nasaBodySmall  = Font.system(size: 13, weight: .regular, design: .default)

    // Label — chips, tags, captions
    static let nasaLabelLarge  = Font.system(size: 14, weight: .medium, design: .default)
    static let nasaLabelMedium = Font.system(size: 12, weight: .medium, design: .default)
    static let nasaLabelSmall  = Font.system(size: 11, weight: .regular, design: .default)

    // Tab bar — Figma: Inter 10 pt (regular / semibold selected); system fallback until Inter is bundled.
    static let nasaTabBarLabel = Font.system(size: 10, weight: .regular, design: .default)
    static let nasaTabBarLabelSelected = Font.system(size: 10, weight: .semibold, design: .default)
}

// MARK: - Dynamic type scaling support
public extension Font {
    /// Responsive font that scales with Dynamic Type.
    static func nasaScaled(_ style: Font.TextStyle, size: CGFloat, weight: Font.Weight = .regular) -> Font {
        .system(size: size, weight: weight, design: .default).leading(.standard)
    }
}
