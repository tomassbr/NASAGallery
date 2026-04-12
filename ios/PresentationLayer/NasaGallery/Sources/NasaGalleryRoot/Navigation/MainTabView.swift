import DashboardFeature
import FavoritesFeature
import SettingsFeature
import SwiftUI
import UIToolkit

// MARK: - MainTabView
// Custom tab bar with gradient-filled icons for active tab (Figma: indigo→teal gradient).
public struct MainTabView: View {
    @State private var selectedTab: NasaTab = .home

    public init() {}

    public var body: some View {
        ZStack(alignment: .bottom) {
            tabContent
                .safeAreaInset(edge: .bottom) { tabBarInsetSpacer }
            NasaTabBar(selectedTab: $selectedTab)
        }
        .ignoresSafeArea(.keyboard)
    }

    // MARK: - Layout

    @ViewBuilder
    private var tabContent: some View {
        switch selectedTab {
        case .home:
            NavigationStack { DashboardFeatureView() }
        case .favorites:
            NavigationStack { FavoritesFeatureView() }
        case .settings:
            NavigationStack { SettingsFeatureView() }
        }
    }

    private var tabBarInsetSpacer: some View {
        Color.clear.frame(height: .tabBarChromeHeight)
    }
}

// MARK: - NasaTabBar

private struct NasaTabBar: View {
    @Binding var selectedTab: NasaTab

    private let activeGradient = LinearGradient(
        colors: [.nasaPrimary, .nasaAccent],
        startPoint: .topLeading,
        endPoint: .bottomTrailing
    )

    var body: some View {
        HStack(spacing: 0) {
            ForEach(NasaTab.allCases, id: \.self) { tab in
                NasaTabBarItem(
                    tab: tab,
                    isSelected: selectedTab == tab,
                    activeGradient: activeGradient
                ) {
                    selectedTab = tab
                }
            }
        }
        .padding(.horizontal, .spaceSM)
        .padding(.top, .spaceSM)
        .padding(.bottom, .spaceXS)
        .frame(maxWidth: .infinity)
        .background {
            Color.nasaSurface
                .overlay(
                    Color.nasaPrimary.opacity(0.08),
                    alignment: .top
                )
                .ignoresSafeArea(edges: .bottom)
        }
        .overlay(alignment: .top) {
            Rectangle()
                .fill(Color.nasaPrimary.opacity(0.15))
                .frame(height: 0.5)
        }
    }
}

// MARK: - NasaTabBarItem

private struct NasaTabBarItem: View {
    let tab: NasaTab
    let isSelected: Bool
    let activeGradient: LinearGradient
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            VStack(spacing: .spaceXS) {
                tabIcon
                tabTitle
            }
            .frame(maxWidth: .infinity)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .animation(.easeInOut(duration: 0.15), value: isSelected)
    }

    private var tabIcon: some View {
        tab.tabBarIcon
            .renderingMode(.template)
            .resizable()
            .scaledToFit()
            .frame(width: .iconSize, height: .iconSize)
            .foregroundStyle(
                isSelected
                    ? AnyShapeStyle(activeGradient)
                    : AnyShapeStyle(Color.nasaSubtle)
            )
    }

    private var tabTitle: some View {
        Text(tab.title)
            .font(isSelected ? .nasaTabBarLabelSelected : .nasaTabBarLabel)
            .foregroundStyle(
                isSelected
                    ? AnyShapeStyle(activeGradient)
                    : AnyShapeStyle(Color.nasaSubtle)
            )
    }
}
