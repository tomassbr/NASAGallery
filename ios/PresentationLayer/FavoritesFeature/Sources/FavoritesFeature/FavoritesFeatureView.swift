import DependencyInjection
import Factory
import KMPShared
import NavigatorUI
import SFSafeSymbols
import SwiftUI
import UIToolkit

public struct FavoritesFeatureView: View {

    @State private var toastData: ToastData?
    @State private var state = FavoritesState(favorites: [], isEmpty: true)
    @State private var undoItem: Favorite?
    @State private var undoTask: Task<Void, Never>?
    @State private var showUndo = false

    @Injected(\.favoritesViewModel) private var viewModel: FavoritesViewModel

    private var favorites: [Favorite] { Array(state.favorites) }

    public init() {}

    public var body: some View {
        ManagedNavigationStack { _ in
            ZStack(alignment: .bottom) {
                mainContent.background(Color.nasaBackground.ignoresSafeArea())
                if showUndo, let item = undoItem {
                    undoSnackbar(for: item)
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                        .padding(.bottom, .spaceLG)
                        .zIndex(1)
                }
            }
            .animation(.easeInOut(duration: 0.25), value: showUndo)
            .toolbar(.hidden, for: .navigationBar)
            .safeAreaInset(edge: .top, spacing: 0) { favoritesHeader }
        }
        .tint(AppTheme.Colors.navBarTitle)
        .toastView($toastData)
        .bindViewModel(viewModel, onEvent: onEvent)
        .task { for await s in viewModel.state { state = s } }
    }

    // MARK: - Header

    private var favoritesHeader: some View {
        NasaPageHeader(title: "Favorites", subtitle: "\(favorites.count) SAVED") {
            Button(action: {}) {
                NasaImageAsset.Icon.sort
                    .renderingMode(.template)
                    .resizable()
                    .scaledToFit()
                    .frame(width: .iconSize, height: .iconSize)
                    .foregroundStyle(Color.nasaOnSurfaceVariant)
                    .frame(width: 40, height: 40)
                    .background(
                        Circle()
                            .fill(Color.nasaSurface)
                            .overlay(Circle().strokeBorder(Color.nasaBorderSubtle, lineWidth: 1))
                    )
            }
            .buttonStyle(.plain)
            .accessibilityLabel("Filter")
        }
    }

    // MARK: - Content

    @ViewBuilder
    private var mainContent: some View {
        if favorites.isEmpty {
            emptyState
        } else {
            favoritesList
        }
    }

    private var emptyState: some View {
        NasaEmptyView(
            title: "No favorites yet",
            message: "Images you save will appear here. Start exploring and tap the heart icon.",
            systemSymbol: .heartSlash
        )
    }

    private var favoritesList: some View {
        ScrollView {
            LazyVStack(spacing: .spaceSM) {
                ForEach(favorites, id: \.nasaId) { favorite in
                    NasaFavoriteRow(
                        title: favorite.title,
                        date: favorite.dateCreated,
                        thumbnailUrl: URL(string: favorite.thumbnailUrl),
                        onRemove: { removeFavorite(favorite) }
                    )
                }
            }
            .padding(.horizontal, .screenHorizontalPadding)
            .padding(.vertical, .spaceSM)
        }
    }

    // MARK: - Undo Snackbar

    private func undoSnackbar(for item: Favorite) -> some View {
        HStack(spacing: .spaceMD) {
            Text("Removed \"\(item.title)\"")
                .font(.nasaBodyMedium)
                .foregroundStyle(Color.nasaOnBackground)
                .lineLimit(1)
            Spacer()
            Button("Undo") { undoRemove(item) }
                .font(.nasaLabelMedium)
                .foregroundStyle(Color.nasaAccent)
        }
        .padding(.horizontal, .spaceMD)
        .padding(.vertical, .spaceSM)
        .background(Color.nasaSurface)
        .clipShape(RoundedRectangle(cornerRadius: .radiusMD))
        .padding(.horizontal, .screenHorizontalPadding)
        .shadow(color: .black.opacity(0.3), radius: 8, y: 4)
    }

    // MARK: - Actions

    private func removeFavorite(_ favorite: Favorite) {
        viewModel.onIntent(FavoritesIntentRemove(nasaId: favorite.nasaId))
        undoItem = favorite
        showUndo = true
        scheduleUndoDismiss()
    }

    private func undoRemove(_ item: Favorite) {
        viewModel.onIntent(FavoritesIntentUndoRemove(nasaId: item.nasaId))
        undoTask?.cancel()
        withAnimation { showUndo = false }
        undoItem = nil
    }

    private func scheduleUndoDismiss() {
        undoTask?.cancel()
        undoTask = Task {
            try? await Task.sleep(nanoseconds: 3_000_000_000)
            await MainActor.run {
                guard !Task.isCancelled else { return }
                withAnimation { showUndo = false }
                undoItem = nil
            }
        }
    }

    // MARK: - Events

    private func onEvent(_ event: FavoritesEvent) {
        switch onEnum(of: event) {
        case .showUndoSnackbar:
            break // snackbar is managed locally via removeFavorite
        case .navigateToDetail:
            break
        case .signInRequired:
            break // TODO: present auth sheet when Add-from-guest is wired from gallery
        }
    }
}
