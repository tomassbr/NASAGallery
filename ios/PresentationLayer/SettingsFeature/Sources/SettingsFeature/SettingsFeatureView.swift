import DependencyInjection
import Factory
import KMPShared
import NavigatorUI
import SFSafeSymbols
import SwiftUI
import UIToolkit

public struct SettingsFeatureView: View {

    @State private var toastData: ToastData?
    @State private var state = ProfileState(apiKey: "DEMO_KEY", darkThemeEnabled: true, dataSaverEnabled: false, isEditingApiKey: false)
    @State private var clearCacheAlert: AlertData?
    @State private var apiKeyDraft = ""

    @Injected(\.profileViewModel) private var viewModel: ProfileViewModel

    public init() {}

    public var body: some View {
        ManagedNavigationStack { _ in
            ScrollView {
                LazyVStack(alignment: .leading, spacing: .spaceLG) {
                    userSection
                    preferencesSection
                    storageSection
                    advancedSection
                }
                .padding(.horizontal, .screenHorizontalPadding)
                .padding(.vertical, .spaceSM)
            }
            .background(Color.nasaBackground.ignoresSafeArea())
            .toolbar(.hidden, for: .navigationBar)
            .safeAreaInset(edge: .top, spacing: 0) { settingsHeader }
            .sheet(isPresented: editingApiKeyBinding) { apiKeyEditorSheet }
        }
        .tint(AppTheme.Colors.navBarTitle)
        .nasaAlert($clearCacheAlert)
        .toastView($toastData)
        .bindViewModel(viewModel, onEvent: onEvent)
        .task { for await s in viewModel.state { state = s } }
    }

    // MARK: - Header

    private var settingsHeader: some View {
        NasaPageHeader(title: "Settings", subtitle: "PREFERENCES")
    }

    // MARK: - Sections

    private var userSection: some View {
        NasaInsetSettingsCard {
            NasaUserProfileCard(name: "Local Explorer", subtitle: "Device Storage")
        }
    }

    private var preferencesSection: some View {
        VStack(alignment: .leading, spacing: .spaceSM) {
            NasaSectionHeader("PREFERENCES")
            NasaInsetSettingsCard {
                NasaSettingsToggleRow(symbol: .moonFill, title: "Dark Theme", isOn: darkThemeBinding)
                settingsRowDivider
                NasaSettingsToggleRow(symbol: .wifiExclamationmark, title: "Data Saver", isOn: dataSaverBinding)
            }
        }
    }

    private var storageSection: some View {
        VStack(alignment: .leading, spacing: .spaceSM) {
            NasaSectionHeader("DATA & STORAGE")
            NasaInsetSettingsCard {
                NasaSettingsActionRow(
                    symbol: .arrowDownCircle,
                    title: "Offline Downloads",
                    value: "0 MB",
                    tint: .nasaSubtle
                )
                settingsRowDivider
                NasaSettingsActionRow(
                    symbol: .trash,
                    title: "Clear Image Cache",
                    tint: .nasaError,
                    titleColor: .nasaError,
                    action: { presentClearCacheAlert() }
                )
            }
        }
    }

    private var advancedSection: some View {
        VStack(alignment: .leading, spacing: .spaceSM) {
            NasaSectionHeader("ADVANCED")
            NasaInsetSettingsCard {
                apiKeyRow
            }
        }
    }

    private var apiKeyRow: some View {
        HStack {
            Label {
                VStack(alignment: .leading, spacing: 2) {
                    Text("NASA API Key")
                        .font(.nasaBodyMedium)
                        .foregroundStyle(Color.nasaOnBackground)
                    Text(state.apiKey)
                        .font(.nasaLabelSmall)
                        .foregroundStyle(Color.nasaSubtle)
                        .monospaced()
                }
            } icon: {
                Image(systemSymbol: .key)
                    .foregroundStyle(Color.nasaSubtle)
            }
            Spacer()
            Button("Edit") {
                apiKeyDraft = state.apiKey
                viewModel.onIntent(ProfileIntentEditApiKey.shared)
            }
            .font(.nasaLabelMedium)
            .foregroundStyle(Color.nasaPrimary)
        }
        .padding(.horizontal, .spaceMD)
        .padding(.vertical, .spaceSM)
    }

    private var settingsRowDivider: some View {
        Rectangle()
            .fill(Color.nasaBorderSubtle)
            .frame(height: 1)
            .padding(.leading, 52)
    }

    // MARK: - API Key Editor Sheet

    private var apiKeyEditorSheet: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: .spaceLG) {
                Text("Enter your NASA API key from api.nasa.gov")
                    .font(.nasaBodyMedium)
                    .foregroundStyle(Color.nasaSubtle)
                TextField("API Key", text: $apiKeyDraft)
                    .font(.nasaBodyMedium)
                    .foregroundStyle(Color.nasaOnBackground)
                    .padding(.spaceMD)
                    .background(Color.nasaSurface)
                    .clipShape(RoundedRectangle(cornerRadius: .radiusMD))
                Spacer()
            }
            .padding(.spaceMD)
            .background(Color.nasaBackground.ignoresSafeArea())
            .navigationTitle("NASA API Key")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        viewModel.onIntent(ProfileIntentCancelApiKeyEdit.shared)
                    }
                    .foregroundStyle(Color.nasaSubtle)
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") {
                        viewModel.onIntent(ProfileIntentSaveApiKey(key: apiKeyDraft))
                    }
                    .font(.nasaLabelMedium)
                    .foregroundStyle(Color.nasaPrimary)
                }
            }
        }
        .presentationDetents([.medium])
    }

    // MARK: - Alerts

    private func presentClearCacheAlert() {
        clearCacheAlert = AlertData(
            title: "Clear Image Cache",
            message: "All cached images will be removed. They will be re-downloaded when needed.",
            primaryAction: AlertData.Action(title: "Clear", style: .destruction) {
                viewModel.onIntent(ProfileIntentClearCache.shared)
                clearCacheAlert = nil
            },
            secondaryAction: AlertData.Action(title: "Cancel", style: .cancel) {
                clearCacheAlert = nil
            }
        )
    }

    // MARK: - Bindings

    private var darkThemeBinding: Binding<Bool> {
        Binding(
            get: { state.darkThemeEnabled },
            set: { viewModel.onIntent(ProfileIntentToggleDarkTheme(enabled: $0)) }
        )
    }

    private var dataSaverBinding: Binding<Bool> {
        Binding(
            get: { state.dataSaverEnabled },
            set: { viewModel.onIntent(ProfileIntentToggleDataSaver(enabled: $0)) }
        )
    }

    private var editingApiKeyBinding: Binding<Bool> {
        Binding(
            get: { state.isEditingApiKey },
            set: { if !$0 { viewModel.onIntent(ProfileIntentCancelApiKeyEdit.shared) } }
        )
    }

    // MARK: - Events

    private func onEvent(_ event: ProfileEvent) {
        switch onEnum(of: event) {
        case .cacheCleared:
            toastData = ToastData("Cache cleared", hideAfter: 2)
        }
    }
}

// MARK: - Grouped settings card (replaces List inset-grouped section chrome)

private struct NasaInsetSettingsCard<Content: View>: View {
    @ViewBuilder var content: () -> Content

    var body: some View {
        VStack(spacing: 0) {
            content()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.nasaSurface)
        .clipShape(RoundedRectangle(cornerRadius: .radiusMD))
    }
}
