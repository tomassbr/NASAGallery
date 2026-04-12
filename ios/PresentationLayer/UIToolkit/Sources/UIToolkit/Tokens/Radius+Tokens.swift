import SwiftUI

// MARK: - NASA Corner Radius Tokens
public extension CGFloat {
    static let radiusXS: CGFloat     = 4
    static let radiusSM: CGFloat     = 8
    static let radiusMD: CGFloat     = 12
    static let radiusCard: CGFloat   = 16
    static let radiusLG: CGFloat     = 20
    static let radiusButton: CGFloat = 24
    static let radiusXL: CGFloat     = 28
    static let radiusFull: CGFloat   = 9999
}

// MARK: - SwiftUI Shape convenience
public extension RoundedRectangle {
    static let card    = RoundedRectangle(cornerRadius: .radiusCard, style: .continuous)
    static let button  = RoundedRectangle(cornerRadius: .radiusButton, style: .continuous)
    static let sm      = RoundedRectangle(cornerRadius: .radiusSM, style: .continuous)
    static let md      = RoundedRectangle(cornerRadius: .radiusMD, style: .continuous)
}
