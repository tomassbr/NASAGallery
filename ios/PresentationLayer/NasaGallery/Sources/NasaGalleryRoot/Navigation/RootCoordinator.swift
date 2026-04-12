import AuthFeature
import DependencyInjection
import Factory
import KMPShared
import SwiftUI
import UIToolkit

// MARK: - RootCoordinator
// Watches auth state via @Injected VM and routes to Auth or MainTabView.
public struct RootCoordinator: View {

    @State private var authState = AuthState(currentUser: nil, isLoading: false, isGuest: false, error: nil)
    @Injected(\.authViewModel) private var authViewModel: AuthViewModel

    public init() {}

    public var body: some View {
        Group {
            if authState.currentUser != nil {
                MainTabView()
                    .transition(.opacity)
            } else {
                AuthFeatureView()
                    .transition(.opacity)
            }
        }
        .animation(.easeInOut(duration: 0.3), value: authState.currentUser != nil)
        .task { for await s in authViewModel.state { authState = s } }
    }
}
