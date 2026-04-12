import SwiftUI

// MARK: - NasaFavoriteRow
// Row for a saved favorite — thumbnail, title, date, remove button (use in `LazyVStack` / lists).
// Used in the Favorites (Saved) screen.
public struct NasaFavoriteRow: View {
    private let title: String
    private let date: String?
    private let thumbnailUrl: URL?
    private let onRemove: () -> Void

    public init(
        title: String,
        date: String? = nil,
        thumbnailUrl: URL?,
        onRemove: @escaping () -> Void
    ) {
        self.title = title
        self.date = date
        self.thumbnailUrl = thumbnailUrl
        self.onRemove = onRemove
    }

    public var body: some View {
        HStack(spacing: .spaceMD) {
            thumbnail
            info
            Spacer()
            FavoriteButton(isFavorited: true, action: onRemove)
        }
        .padding(.spaceSM)
        .background(Color.nasaSurface)
        .clipShape(RoundedRectangle(cornerRadius: .radiusMD))
    }

    private var thumbnail: some View {
        NasaAsyncImage(url: thumbnailUrl, contentMode: .fill, cornerRadius: .radiusSM)
            .frame(width: 72, height: 56)
            .clipped()
    }

    private var info: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.nasaTitleMedium)
                .foregroundStyle(Color.nasaOnBackground)
                .lineLimit(2)
            if let date {
                Text(date)
                    .font(.nasaLabelSmall)
                    .foregroundStyle(Color.nasaSubtle)
            }
        }
    }
}
