import SwiftUI

// MARK: - MediaGrid
// Adaptive 2-column grid with lazy loading trigger at the bottom.
public struct MediaGrid<Item: Identifiable, Content: View>: View {
    private let items: [Item]
    private let hasMore: Bool
    private let isLoadingMore: Bool
    private let onLoadMore: () -> Void
    private let content: (Item) -> Content

    private let columns = [
        GridItem(.flexible(), spacing: .spaceSM),
        GridItem(.flexible(), spacing: .spaceSM)
    ]

    public init(
        items: [Item],
        hasMore: Bool,
        isLoadingMore: Bool,
        onLoadMore: @escaping () -> Void,
        @ViewBuilder content: @escaping (Item) -> Content
    ) {
        self.items = items
        self.hasMore = hasMore
        self.isLoadingMore = isLoadingMore
        self.onLoadMore = onLoadMore
        self.content = content
    }

    public var body: some View {
        LazyVGrid(columns: columns, spacing: .spaceSM) {
            ForEach(items) { item in
                content(item)
                    .onAppear {
                        if item.id == items.last?.id && hasMore {
                            onLoadMore()
                        }
                    }
            }

            if isLoadingMore {
                NasaLoadingFooter()
                    .gridCellColumns(2)
            }
        }
        .padding(.horizontal, .screenHorizontalPadding)
    }
}
