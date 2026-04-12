import SwiftUI

// MARK: - FavoriteButton
// Animated heart toggle. Uses template asset `ic-heart` from UIToolkit.
public struct FavoriteButton: View {
    private let isFavorited: Bool
    private let action: () -> Void
    private let size: CGFloat

    public init(isFavorited: Bool, size: CGFloat = .iconSize, action: @escaping () -> Void) {
        self.isFavorited = isFavorited
        self.size = size
        self.action = action
    }

    public var body: some View {
        Button(action: action) {
            NasaImageAsset.TabBar.heart
                .renderingMode(.template)
                .resizable()
                .scaledToFit()
                .frame(width: size * 0.75, height: size * 0.75)
                .foregroundStyle(isFavorited ? Color.nasaError : Color.nasaOnSurface)
                .scaleEffect(isFavorited ? 1.06 : 1.0)
                .animation(.spring(response: 0.32, dampingFraction: 0.65), value: isFavorited)
                .frame(width: size, height: size)
                .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityLabel(isFavorited ? "Remove from favorites" : "Add to favorites")
        .accessibilityAddTraits(.isButton)
    }
}
