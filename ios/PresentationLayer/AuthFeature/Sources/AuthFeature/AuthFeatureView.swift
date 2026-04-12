import DependencyInjection
import Factory
import KMPShared
import NavigatorUI
import SFSafeSymbols
import SwiftUI
import UIToolkit

public struct AuthFeatureView: View {

    @State private var toastData: ToastData?
    @State private var state = AuthState(currentUser: nil, isLoading: false, isGuest: false, error: nil)
    @State private var errorAlert: AlertData?

    @Injected(\.authViewModel) private var viewModel: AuthViewModel

    public init() {}

    public var body: some View {
        ZStack {
            Color.nasaBackground.ignoresSafeArea()
            scrollContent
            if state.isLoading { loadingOverlay }
        }
        .onChange(of: state.error, perform: { error in
            if let error {
                errorAlert = AlertData(
                    title: "Sign In Failed",
                    message: error.localizedMessage.localized(),
                    primaryAction: AlertData.Action(
                        title: MR.strings().dialog_error_close_text.toLocalized(),
                        style: .default,
                        handler: { errorAlert = nil }
                    )
                )
            } else {
                errorAlert = nil
            }
        })
        .nasaAlert($errorAlert)
        .toastView($toastData)
        .bindViewModel(viewModel, onEvent: onEvent)
        .task { for await s in viewModel.state { state = s } }
    }

    // MARK: - Layout

    private var scrollContent: some View {
        ScrollView(.vertical, showsIndicators: false) {
            VStack(spacing: 0) {
                Spacer(minLength: .spaceXXXL)
                NasaLogoView().padding(.bottom, .spaceXL)
                titleSection.padding(.bottom, .spaceXXL)
                buttonSection
                    .padding(.horizontal, .screenHorizontalPadding)
                    .padding(.bottom, .spaceLG)
                infoText
                Spacer(minLength: .spaceXXL)
            }
        }
    }

    // MARK: - Sections

    private var titleSection: some View {
        VStack(spacing: .spaceSM) {
            Text("NASA Gallery")
                .font(.nasaDisplayMedium)
                .foregroundStyle(Color.nasaOnBackground)
            Text("Explore the universe through stunning\nimages from NASA's archives")
                .font(.nasaBodyMedium)
                .foregroundStyle(Color.nasaSubtle)
                .multilineTextAlignment(.center)
        }
    }

    private var buttonSection: some View {
        VStack(spacing: .spaceMD) {
            NasaSignInButton(style: .apple, isDisabled: state.isLoading) {
                viewModel.onIntent(AuthIntentSignInWithApple.shared)
            }
            NasaSignInButton(style: .google, isDisabled: state.isLoading) {
                viewModel.onIntent(AuthIntentSignInWithGoogle.shared)
            }
            orDivider
            guestButton
        }
    }

    private var orDivider: some View {
        HStack(spacing: .spaceMD) {
            Rectangle().fill(Color.nasaSurface).frame(height: 1)
            Text("or")
                .font(.nasaLabelSmall)
                .foregroundStyle(Color.nasaSubtle)
            Rectangle().fill(Color.nasaSurface).frame(height: 1)
        }
        .padding(.vertical, .spaceSM)
    }

    private var guestButton: some View {
        Button(action: { viewModel.onIntent(AuthIntentSignInAsGuest.shared) }) {
            Text("Continue as Guest")
                .font(.nasaTitleMedium)
                .foregroundStyle(Color.nasaAccent)
                .frame(maxWidth: .infinity)
                .frame(height: 48)
        }
        .disabled(state.isLoading)
    }

    private var infoText: some View {
        Text("Guests can browse images but cannot save favorites.")
            .font(.nasaLabelSmall)
            .foregroundStyle(Color.nasaSubtle)
            .multilineTextAlignment(.center)
            .padding(.horizontal, .spaceXL)
    }

    private var loadingOverlay: some View {
        ZStack {
            Color.black.opacity(0.5).ignoresSafeArea()
            NasaLoadingView()
        }
    }

    // MARK: - Events

    private func onEvent(_ event: AuthEvent) {
        switch onEnum(of: event) {
        case .navigateToMain:
            break // handled by RootCoordinator watching auth state
        case .navigateToAuth:
            break
        case .showSignInSheet:
            break
        }
    }
}
