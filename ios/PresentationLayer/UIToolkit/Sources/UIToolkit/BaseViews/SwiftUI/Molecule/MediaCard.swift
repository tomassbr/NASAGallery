import SwiftUI

// MARK: - MediaCard
// Reusable card for gallery items, search results, and favorites.
// Thumbnail fills the card with a gradient overlay showing the title.
public struct MediaCard: View {
    private let nasaId: String
    private let title: String
    private let thumbnailUrl: URL?
    private let date: String?
    private let isFavorited: Bool
    private let onTap: () -> Void
    private let onFavorite: (() -> Void)?

    public init(
        nasaId: String,
        title: String,
        thumbnailUrl: URL?,
        date: String? = nil,
        isFavorited: Bool = false,
        onTap: @escaping () -> Void,
        onFavorite: (() -> Void)? = nil
    ) {
        self.nasaId = nasaId
        self.title = title
        self.thumbnailUrl = thumbnailUrl
        self.date = date
        self.isFavorited = isFavorited
        self.onTap = onTap
        self.onFavorite = onFavorite
    }

    public var body: some View {
        Button(action: onTap) {
            ZStack(alignment: .bottomLeading) {
                NasaAsyncImage(url: thumbnailUrl, contentMode: .fill, cornerRadius: .radiusCard)
                    .aspectRatio(1, contentMode: .fill)
                    .clipped()

                // Gradient overlay
                LinearGradient(
                    colors: [.clear, .black.opacity(0.65)],
                    startPoint: .center,
                    endPoint: .bottom
                )
                .clipShape(RoundedRectangle.card)

                // Bottom content
                VStack(alignment: .leading, spacing: .spaceXXS) {
                    Text(title)
                        .font(.nasaTitleSmall)
                        .foregroundStyle(.white)
                        .lineLimit(2)

                    if let date {
                        Text(date)
                            .font(.nasaLabelSmall)
                            .foregroundStyle(.white.opacity(0.7))
                    }
                }
                .padding(.cardPadding)
                .frame(maxWidth: .infinity, alignment: .leading)

                // Favorite button overlay
                if let onFavorite {
                    VStack {
                        HStack {
                            Spacer()
                            FavoriteButton(isFavorited: isFavorited, size: 22) { onFavorite() }
                                .padding(.spaceXS)
                        }
                        Spacer()
                    }
                    .padding(.spaceXS)
                }
            }
        }
        .buttonStyle(.plain)
        .clipShape(RoundedRectangle.card)
        .nasaShadow(.card)
    }
}
