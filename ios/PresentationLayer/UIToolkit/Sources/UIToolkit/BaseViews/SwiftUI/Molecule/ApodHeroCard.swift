import SwiftUI

// MARK: - ApodHeroCard
// Full-width hero card for the APOD (Today) screen.
// Shows image with bottom gradient overlay (title + description + actions) and a top-left badge.
public struct ApodHeroCard: View {
    private let title: String
    private let imageUrl: URL?
    private let date: String
    private let explanation: String
    private let copyright: String?
    private let isFavorited: Bool
    private let onFavorite: () -> Void
    private let onShare: () -> Void
    private let onViewFullscreen: () -> Void

    public init(
        title: String,
        imageUrl: URL?,
        date: String,
        explanation: String,
        isFavorited: Bool,
        onFavorite: @escaping () -> Void,
        onShare: @escaping () -> Void,
        onViewFullscreen: @escaping () -> Void,
        copyright: String? = nil
    ) {
        self.title = title
        self.imageUrl = imageUrl
        self.date = date
        self.explanation = explanation
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

            // Bottom gradient + text overlay
            LinearGradient(
                colors: [.clear, .black.opacity(0.85)],
                startPoint: .center,
                endPoint: .bottom
            )
            .overlay(alignment: .bottom) { labelsStack }

            // Top-left "APOD • date" badge
            VStack {
                HStack {
                    apodBadge
                    Spacer()
                }
                Spacer()
            }
            .padding(.cardPadding)
        }
        .clipShape(RoundedRectangle.card)
        .nasaShadow(.lg)
    }

    // MARK: - Subviews

    private var labelsStack: some View {
        VStack(alignment: .leading, spacing: .spaceXS) {
            Text(title)
                .font(.nasaHeadlineMedium)
                .foregroundStyle(.white)
                .lineLimit(2)

            Text(explanation)
                .font(.nasaBodySmall)
                .foregroundStyle(.white.opacity(0.75))
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

    private var apodBadge: some View {
        Text("APOD • \(formattedDate)")
            .font(.nasaLabelSmall)
            .monospaced()
            .foregroundStyle(.white.opacity(0.9))
            .padding(.horizontal, .spaceSM)
            .padding(.vertical, .spaceXS)
            .background(Color.nasaMediaOverlay)
            .overlay(
                RoundedRectangle(cornerRadius: .radiusSM)
                    .strokeBorder(Color.nasaBorderSubtle)
            )
            .clipShape(RoundedRectangle(cornerRadius: .radiusSM))
    }

    // MARK: - Helpers

    private var formattedDate: String {
        guard let d = ApodHeroCard.inputFormatter.date(from: date) else { return date }
        return ApodHeroCard.outputFormatter.string(from: d)
    }

    private static let inputFormatter: DateFormatter = {
        let f = DateFormatter()
        f.dateFormat = "yyyy-MM-dd"
        f.locale = Locale(identifier: "en_US_POSIX")
        return f
    }()

    private static let outputFormatter: DateFormatter = {
        let f = DateFormatter()
        f.dateFormat = "MMM d"
        f.locale = Locale(identifier: "en_US_POSIX")
        return f
    }()
}
