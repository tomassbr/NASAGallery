import SFSafeSymbols
import SwiftUI

// MARK: - NasaLogoView
// Circular branded logo — globe icon on primary-tinted ring.
// Used on the Auth screen and splash states.
public struct NasaLogoView: View {
    private let size: CGFloat

    public init(size: CGFloat = 96) {
        self.size = size
    }

    public var body: some View {
        ZStack {
            Circle()
                .fill(Color.nasaPrimary.opacity(0.15))
                .frame(width: size, height: size)
            Circle()
                .strokeBorder(Color.nasaPrimary.opacity(0.3), lineWidth: 1.5)
                .frame(width: size, height: size)
            Image(systemSymbol: .globeAmericasFill)
                .font(.system(size: size * 0.42))
                .foregroundStyle(Color.nasaPrimary)
        }
    }
}
