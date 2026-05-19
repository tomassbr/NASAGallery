import DependencyInjection
import Factory
import KMPShared
import NavigatorUI
import SwiftUI
import UIToolkit

public struct DashboardFeatureView: View {

    @State private var toastData: ToastData?
    @State private var apodState = ApodState(apod: nil, isLoading: false, error: nil, selectedDate: nil)
    @State private var galleryState = GalleryState(items: [], isLoading: false, isLoadingMore: false, hasMore: true, error: nil)
    @State private var selectedFilter = "All Sources"
    @State private var selectedNasaId: String?
    @State private var showApodDetail = false
    @State private var showComingSoon = false

    @Injected(\.apodViewModel) private var apodViewModel: ApodViewModel
    @Injected(\.galleryViewModel) private var galleryViewModel: GalleryViewModel

    private let filterOptions = ["All Sources", "APOD", "Mars Rovers", "EPIC"]

    public init() {}

    public var body: some View {
        ManagedNavigationStack { _ in
            ScrollView(.vertical, showsIndicators: false) {
                VStack(alignment: .leading, spacing: .spaceLG) {
                    filterSection
                    todaysPickSection
                    exploreSection
                }
                .padding(.top, .spaceLG)
                .padding(.bottom, .spaceMD)
            }
            .background(Color.nasaBackground.ignoresSafeArea())
            .safeAreaInset(edge: .top, spacing: 0) {
                dashboardHeaderBar
            }
            .toolbar(.hidden, for: .navigationBar)
            .sheet(isPresented: Binding(
                get: { selectedNasaId != nil },
                set: { if !$0 { selectedNasaId = nil } }
            )) {
                if let nasaId = selectedNasaId {
                    NavigationStack { mediaDetailView(nasaId: nasaId) }
                        .background(Color.nasaBackground)
                }
            }
            .sheet(isPresented: $showApodDetail) {
                NavigationStack { apodDetailView }
                    .background(Color.nasaBackground)
            }
            .alert("Coming Soon", isPresented: $showComingSoon) {
                Button("OK", role: .cancel) {}
            } message: {
                Text("This feature will be available in a future update.")
            }
        }
        .toastView($toastData)
        .bindViewModel(apodViewModel, onEvent: onApodEvent)
        .task { for await s in apodViewModel.state { apodState = s } }
        .task {
            galleryViewModel.onViewAppeared()
            for await s in galleryViewModel.state { galleryState = s }
        }
    }

    // MARK: - Header

    private var dashboardHeaderBar: some View {
        HStack(alignment: .center, spacing: .spaceMD) {
            VStack(alignment: .leading, spacing: 2) {
                Text("NASA Gallery")
                    .font(.system(size: 20, weight: .medium))
                    .tracking(-0.5)
                    .foregroundStyle(Color.nasaOnBackground)
                    .lineLimit(2)
                    .minimumScaleFactor(0.85)
                    .multilineTextAlignment(.leading)
                Text("EXPLORATION DATA")
                    .font(.system(size: 12, weight: .regular))
                    .foregroundStyle(Color.nasaSubtle)
                    .tracking(1.2)
                    .lineLimit(1)
                    .minimumScaleFactor(0.8)
            }
            .frame(minWidth: 0, maxWidth: .infinity, alignment: .leading)
            .layoutPriority(1)
            .accessibilityElement(children: .combine)

            dashboardSearchButton
        }
        .padding(.horizontal, .spaceLG)
        .padding(.top, .spaceMD)
        .padding(.bottom, 17)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.nasaBackground)
    }

    private var dashboardSearchButton: some View {
        Button(action: { showComingSoon = true }) {
            NasaImageAsset.Icon.search
                .renderingMode(.template)
                .resizable()
                .scaledToFit()
                .frame(width: 20, height: 20)
                .foregroundStyle(Color.nasaOnSurfaceVariant)
                .frame(width: 40, height: 40)
                .background(
                    Circle()
                        .fill(Color.nasaSurface)
                        .overlay(
                            Circle()
                                .strokeBorder(Color.white.opacity(0.04), lineWidth: 1)
                        )
                )
                .shadow(color: Color.black.opacity(0.4), radius: 6, x: 4, y: 4)
                .shadow(color: Color.white.opacity(0.02), radius: 4, x: -1, y: -1)
        }
        .buttonStyle(.plain)
        .accessibilityLabel("Search")
    }

    // MARK: - Filter Section

    private var filterSection: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: .space12) {
                ForEach(filterOptions, id: \.self) { option in
                    NasaChip(
                        title: option,
                        isSelected: selectedFilter == option,
                        action: {
                            if option == "All Sources" {
                                selectedFilter = option
                            } else {
                                showComingSoon = true
                            }
                        }
                    )
                }
            }
            .padding(.horizontal, .screenHorizontalPadding)
        }
    }

    // MARK: - Today's Pick

    private var todaysPickSection: some View {
        VStack(alignment: .leading, spacing: .spaceSM) {
            Text("Today's Pick")
                .font(.nasaHeadlineMedium)
                .foregroundStyle(Color.nasaOnBackground)
                .padding(.horizontal, .screenHorizontalPadding)
            apodContent.padding(.horizontal, .screenHorizontalPadding)
        }
    }

    @ViewBuilder
    private var apodContent: some View {
        if apodState.isLoading {
            RoundedRectangle(cornerRadius: .radiusCard)
                .fill(Color.nasaSurface)
                .frame(height: 280)
                .opacity(0.5)
        } else if let apod = apodState.apod {
            ApodHeroCard(
                title: apod.title,
                imageUrl: URL(string: apod.displayUrl),
                date: apod.date,
                explanation: apod.explanation,
                isFavorited: false,
                onFavorite: { showComingSoon = true },
                onShare: { apodViewModel.onIntent(ApodIntentShare.shared) },
                onViewFullscreen: { showApodDetail = true },
                copyright: apod.copyright
            )
        } else if let error = apodState.error {
            NasaErrorView(
                message: error.localizedMessage.localized(),
                onRetry: { apodViewModel.onIntent(ApodIntentDismissError.shared) }
            )
            .frame(height: 200)
        }
    }

    // MARK: - Explore Section

    private var exploreSection: some View {
        VStack(alignment: .leading, spacing: .spaceSM) {
            exploreSectionHeader.padding(.horizontal, .screenHorizontalPadding)
            galleryCarousel
        }
    }

    private var exploreSectionHeader: some View {
        HStack {
            VStack(alignment: .leading, spacing: 2) {
                Text("Explore")
                    .font(.nasaHeadlineMedium)
                    .foregroundStyle(Color.nasaOnBackground)
                Text("NASA IMAGERY")
                    .font(.nasaLabelSmall)
                    .foregroundStyle(Color.nasaSubtle)
                    .tracking(1.5)
            }
            Spacer()
            Button("See all") { showComingSoon = true }
                .font(.nasaLabelMedium)
                .foregroundStyle(Color.nasaAccent)
        }
    }

    @ViewBuilder
    private var galleryCarousel: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: .spaceSM) {
                if galleryState.isLoading && galleryState.items.isEmpty {
                    ForEach(0..<5, id: \.self) { _ in
                        RoundedRectangle(cornerRadius: .radiusMD)
                            .fill(Color.nasaSurface)
                            .frame(width: 160, height: 120)
                            .opacity(0.5)
                    }
                } else {
                    ForEach(Array(galleryState.items.prefix(10)), id: \.nasaId) { item in
                        GalleryCarouselCard(item: item) { selectedNasaId = item.nasaId }
                    }
                }
            }
            .padding(.horizontal, .screenHorizontalPadding)
        }
    }

    // MARK: - Sheet: Gallery Detail

    @ViewBuilder
    private func mediaDetailView(nasaId: String) -> some View {
        let item = galleryState.items.first(where: { $0.nasaId == nasaId })
        MediaDetailView(item: item, nasaId: nasaId)
    }

    // MARK: - Sheet: APOD Detail

    @ViewBuilder
    private var apodDetailView: some View {
        if let apod = apodState.apod {
            ApodDetailView(apod: apod)
        }
    }

    // MARK: - Events

    private func onApodEvent(_ event: ApodEvent) {
        switch onEnum(of: event) {
        case .share(let data):
            toastData = ToastData("Sharing: \(data.apod.title)", hideAfter: 2)
        case .openFullscreen:
            break
        }
    }
}

// MARK: - Gallery Carousel Card

private struct GalleryCarouselCard: View {
    let item: GalleryItem
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            ZStack(alignment: .bottomLeading) {
                thumbnail
                gradient
                itemTitle
            }
            .frame(width: 160, height: 120)
            .clipShape(RoundedRectangle(cornerRadius: .radiusMD))
        }
        .buttonStyle(.plain)
    }

    private var thumbnail: some View {
        NasaAsyncImage(url: URL(string: item.thumbnailUrl), contentMode: .fill, cornerRadius: .radiusMD)
            .frame(width: 160, height: 120)
            .clipped()
    }

    private var gradient: some View {
        LinearGradient(
            colors: [Color.black.opacity(0.7), Color.clear],
            startPoint: .bottom,
            endPoint: .center
        )
        .clipShape(RoundedRectangle(cornerRadius: .radiusMD))
    }

    private var itemTitle: some View {
        Text(item.title)
            .font(.nasaLabelSmall)
            .foregroundStyle(.white)
            .lineLimit(2)
            .padding(.spaceSM)
    }
}

// MARK: - Gallery Media Detail Sheet

private struct MediaDetailView: View {
    let item: GalleryItem?
    let nasaId: String
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: .spaceMD) {
                heroImage
                detailContent.padding(.horizontal, .screenHorizontalPadding)
            }
        }
        .background(Color.nasaBackground.ignoresSafeArea())
        .navigationTitle(item?.title ?? nasaId)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button("Done") { dismiss() }
                    .foregroundStyle(Color.nasaPrimary)
            }
        }
    }

    private var heroImage: some View {
        NasaAsyncImage(
            url: URL(string: item?.thumbnailUrl ?? ""),
            contentMode: .fill,
            cornerRadius: 0
        )
        .frame(maxWidth: .infinity)
        .frame(height: 280)
        .clipped()
    }

    private var detailContent: some View {
        VStack(alignment: .leading, spacing: .spaceSM) {
            Text(item?.title ?? nasaId)
                .font(.nasaTitleMedium)
                .foregroundStyle(Color.nasaOnBackground)
            if let date = item?.dateCreated {
                Text(date)
                    .font(.nasaLabelSmall)
                    .foregroundStyle(Color.nasaSubtle)
            }
        }
    }
}

// MARK: - APOD Detail Sheet

private struct ApodDetailView: View {
    let apod: Apod
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: .spaceMD) {
                heroImage
                content.padding(.horizontal, .screenHorizontalPadding)
                Spacer(minLength: .spaceLG)
            }
            .padding(.bottom, .spaceLG)
        }
        .background(Color.nasaBackground.ignoresSafeArea())
        .navigationTitle("Astronomy Picture of the Day")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button("Done") { dismiss() }
                    .foregroundStyle(Color.nasaPrimary)
            }
        }
    }

    private var heroImage: some View {
        NasaAsyncImage(
            url: URL(string: apod.displayUrl),
            contentMode: .fill,
            cornerRadius: 0
        )
        .frame(maxWidth: .infinity)
        .aspectRatio(4 / 3, contentMode: .fit)
        .clipped()
    }

    private var content: some View {
        VStack(alignment: .leading, spacing: .spaceSM) {
            Text(apod.title)
                .font(.nasaTitleMedium)
                .foregroundStyle(Color.nasaOnBackground)
            Text(apod.date)
                .font(.nasaLabelSmall)
                .foregroundStyle(Color.nasaSubtle)
            Text(apod.explanation)
                .font(.nasaBodyMedium)
                .foregroundStyle(Color.nasaOnSurfaceVariant)
                .fixedSize(horizontal: false, vertical: true)
            if let copyright = apod.copyright {
                Text("© \(copyright)")
                    .font(.nasaLabelSmall)
                    .foregroundStyle(Color.nasaSubtle)
            }
        }
    }
}
