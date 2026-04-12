import SFSafeSymbols
import SwiftUI

// MARK: - NasaSignInButton
// Branded sign-in button. Supports Apple (black) and Google (white/bordered) styles.
public struct NasaSignInButton: View {
    public enum Style {
        case apple
        case google
    }

    private let style: Style
    private let action: () -> Void
    private let isDisabled: Bool

    public init(style: Style, isDisabled: Bool = false, action: @escaping () -> Void) {
        self.style = style
        self.isDisabled = isDisabled
        self.action = action
    }

    public var body: some View {
        Button(action: action) {
            HStack(spacing: .spaceSM) {
                icon
                label
            }
            .frame(maxWidth: .infinity)
            .frame(height: 56)
            .background(background)
            .clipShape(RoundedRectangle.button)
            .overlay(border)
        }
        .disabled(isDisabled)
    }

    @ViewBuilder
    private var icon: some View {
        switch style {
        case .apple:
            Image(systemSymbol: .applelogo)
                .font(.system(size: 18, weight: .medium))
                .foregroundStyle(.white)
        case .google:
            Text("G")
                .font(.system(size: 18, weight: .bold))
                .foregroundStyle(Color(red: 0.26, green: 0.52, blue: 0.96))
        }
    }

    private var label: some View {
        Text(style == .apple ? "Sign in with Apple" : "Continue with Google")
            .font(.nasaTitleMedium)
            .foregroundStyle(style == .apple ? Color.white : Color(red: 0.13, green: 0.13, blue: 0.13))
    }

    @ViewBuilder
    private var background: some View {
        switch style {
        case .apple: Color.black
        case .google: Color.white
        }
    }

    @ViewBuilder
    private var border: some View {
        if style == .google {
            RoundedRectangle.button
                .strokeBorder(Color.gray.opacity(0.2), lineWidth: 1)
        }
    }
}
