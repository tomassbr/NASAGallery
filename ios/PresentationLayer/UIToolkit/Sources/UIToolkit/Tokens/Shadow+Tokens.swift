import SwiftUI

// MARK: - NASA Shadow Tokens
public struct NasaShadow {
    public let color: Color
    public let radius: CGFloat
    public let x: CGFloat
    public let y: CGFloat

    public static let none    = NasaShadow(color: .clear, radius: 0, x: 0, y: 0)
    public static let sm      = NasaShadow(color: .black.opacity(0.08), radius: 4, x: 0, y: 2)
    public static let md      = NasaShadow(color: .black.opacity(0.12), radius: 8, x: 0, y: 4)
    public static let lg      = NasaShadow(color: .black.opacity(0.16), radius: 16, x: 0, y: 8)
    public static let card    = NasaShadow(color: .black.opacity(0.10), radius: 12, x: 0, y: 4)
}

public extension View {
    func nasaShadow(_ shadow: NasaShadow) -> some View {
        self.shadow(color: shadow.color, radius: shadow.radius, x: shadow.x, y: shadow.y)
    }
}
