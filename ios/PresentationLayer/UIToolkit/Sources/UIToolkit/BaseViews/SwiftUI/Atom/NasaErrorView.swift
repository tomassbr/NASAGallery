import SFSafeSymbols
import SwiftUI

// MARK: - NasaErrorView
// Error state with icon, message, and optional retry button.
public struct NasaErrorView: View {
    private let message: String
    private let onRetry: (() -> Void)?

    public init(message: String, onRetry: (() -> Void)? = nil) {
        self.message = message
        self.onRetry = onRetry
    }

    public var body: some View {
        VStack(spacing: .spaceMD) {
            Image(systemSymbol: .exclamationmarkTriangle)
                .font(.system(size: 40))
                .foregroundStyle(Color.nasaWarning)

            Text(message)
                .font(.nasaBodyMedium)
                .foregroundStyle(Color.nasaOnSurfaceVariant)
                .multilineTextAlignment(.center)
                .padding(.horizontal, .spaceXL)

            if let onRetry {
                Button("Retry", action: onRetry)
                    .buttonStyle(NasaPrimaryButtonStyle())
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

// MARK: - NasaEmptyView
public struct NasaEmptyView: View {
    private let title: String
    private let message: String
    private let systemSymbol: SFSymbol

    public init(
        title: String,
        message: String,
        systemSymbol: SFSymbol = .trayFill
    ) {
        self.title = title
        self.message = message
        self.systemSymbol = systemSymbol
    }

    public var body: some View {
        VStack(spacing: .spaceMD) {
            Image(systemSymbol: systemSymbol)
                .font(.system(size: 48))
                .foregroundStyle(Color.nasaSubtle)

            Text(title)
                .font(.nasaHeadlineMedium)
                .foregroundStyle(Color.nasaOnSurface)

            Text(message)
                .font(.nasaBodyMedium)
                .foregroundStyle(Color.nasaOnSurfaceVariant)
                .multilineTextAlignment(.center)
                .padding(.horizontal, .spaceXXL)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

// MARK: - Primary Button Style
public struct NasaPrimaryButtonStyle: ButtonStyle {
    public init() {}

    public func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.nasaTitleMedium)
            .foregroundStyle(Color.nasaOnPrimary)
            .padding(.horizontal, .spaceLG)
            .padding(.vertical, .spaceSM)
            .background(
                Color.nasaPrimary.opacity(configuration.isPressed ? 0.8 : 1.0)
            )
            .clipShape(RoundedRectangle.button)
            .scaleEffect(configuration.isPressed ? 0.97 : 1.0)
            .animation(.easeInOut(duration: 0.15), value: configuration.isPressed)
    }
}
