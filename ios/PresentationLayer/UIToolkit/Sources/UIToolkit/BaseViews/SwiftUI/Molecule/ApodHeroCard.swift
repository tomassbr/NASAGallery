import SwiftUI

// MARK: - ApodHeroCard
// Full-width hero card for the APOD (Today) screen.
// Shows full image with gradient + title/date overlay at the bottom.
public struct ApodHeroCard: View {
    private let title: String
    private let imageUrl: URL?
    private let date: String
    private let copyright: String?
    private let isFavorited: Bool
    private let onFavorite: () -> Void
    private let onShare: () -> Void
    private let onViewFullscreen: () -> Void

    public init(
        title: String,
        imageUrl: URL?,
        date: String,
        isFavorited: Bool,
        onFavorite: @escaping () -> Void,
        onShare: @escaping () -> Void,
        onViewFullscreen: @escaping () -> Void,
        copyright: String? = nil,
    ) {
        self.title = title
        self.imageUrl = imageUrl
        self.date = date
        self.isFavorited = isFavorited
        self.onFavorite = onFavorite
        self.onShare = onShare
        self.onViewFullscreen = onViewFullscreen
        self.copyright = copyright
    }

    public var body: some View {
        ZStack(alignment: .bottom) {
            Button(action: onViewFullscreen) {
                NasaAsyncImage(url: imageUrl, contentMode: .fill, cornerRadius: .radiusCard)
                    .aspectRatio(4 / 3, contentMode: .fit)
            }
            .buttonStyle(.plain)

            // Gradient + controls overlay
            VStack(spacing: 0) {
                Spacer()
                LinearGradient(
                    colors: [.clear, .black.opacity(0.8)],
                    startPoint: .top,
                    endPoint: .bottom
                )
                .frame(height: 160)
                .overlay(alignment: .bottom) {
                    VStack(alignment: .leading, spacing: .spaceXS) {
                        Text(date)
                            .font(.nasaLabelMedium)
                            .foregroundStyle(.white.opacity(0.7))

                        Text(title)
                            .font(.nasaHeadlineMedium)
                            .foregroundStyle(.white)
                            .lineLimit(2)

                        if let copyright {
                            Text("© \(copyright)")
                                .font(.nasaLabelSmall)
                                .foregroundStyle(.white.opacity(0.5))
                        }

                        HStack {
                            Spacer()
                            FavoriteButton(isFavorited: isFavorited, size: .iconSizeLG, action: onFavorite)
                            Button(action: onShare) {
                                NasaImageAsset.Icon.share
                                    .renderingMode(.template)
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: .iconSize * 0.75, height: .iconSize * 0.75)
                                    .foregroundStyle(.white)
                                    .frame(width: .iconSizeLG, height: .iconSizeLG)
                            }
                        }
                    }
                    .padding(.cardPadding)
                }
            }
        }
        .clipShape(RoundedRectangle.card)
        .nasaShadow(.lg)
    }
}
