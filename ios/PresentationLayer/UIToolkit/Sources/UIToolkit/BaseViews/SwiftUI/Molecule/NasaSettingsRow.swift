import SFSafeSymbols
import SwiftUI

// MARK: - NasaSettingsActionRow
// Settings row with icon label + optional trailing value + chevron.
// Use for navigable or tappable rows (e.g. "API Key", "Downloads").
public struct NasaSettingsActionRow: View {
    private let symbol: SFSymbol
    private let title: String
    private let subtitle: String?
    private let value: String?
    private let tint: Color
    private let titleColor: Color
    private let action: (() -> Void)?

    public init(
        symbol: SFSymbol,
        title: String,
        subtitle: String? = nil,
        value: String? = nil,
        tint: Color = .nasaPrimary,
        titleColor: Color = .nasaOnBackground,
        action: (() -> Void)? = nil
    ) {
        self.symbol = symbol
        self.title = title
        self.subtitle = subtitle
        self.value = value
        self.tint = tint
        self.titleColor = titleColor
        self.action = action
    }

    public var body: some View {
        Group {
            if let action {
                Button(action: action) { rowContent }
                    .buttonStyle(.plain)
            } else {
                rowContent
            }
        }
        .padding(.horizontal, .spaceMD)
        .padding(.vertical, .spaceSM)
    }

    private var rowContent: some View {
        HStack {
            rowLabel
            Spacer()
            if let value {
                Text(value)
                    .font(.nasaLabelMedium)
                    .foregroundStyle(Color.nasaSubtle)
            }
            Image(systemSymbol: .chevronRight)
                .font(.system(size: 12, weight: .semibold))
                .foregroundStyle(Color.nasaSubtle)
        }
    }

    private var rowLabel: some View {
        Label {
            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(.nasaBodyMedium)
                    .foregroundStyle(titleColor)
                if let subtitle {
                    Text(subtitle)
                        .font(.nasaLabelSmall)
                        .foregroundStyle(Color.nasaSubtle)
                        .monospaced()
                }
            }
        } icon: {
            Image(systemSymbol: symbol)
                .foregroundStyle(tint)
        }
    }
}

// MARK: - NasaSettingsToggleRow
// Settings row with icon label + toggle.
public struct NasaSettingsToggleRow: View {
    private let symbol: SFSymbol
    private let title: String
    private let tint: Color
    @Binding private var isOn: Bool

    public init(
        symbol: SFSymbol,
        title: String,
        tint: Color = .nasaPrimary,
        isOn: Binding<Bool>
    ) {
        self.symbol = symbol
        self.title = title
        self.tint = tint
        self._isOn = isOn
    }

    public var body: some View {
        Toggle(isOn: $isOn) {
            Label {
                Text(title)
                    .font(.nasaBodyMedium)
                    .foregroundStyle(Color.nasaOnBackground)
            } icon: {
                Image(systemSymbol: symbol)
                    .foregroundStyle(tint)
            }
        }
        .tint(.nasaPrimary)
        .padding(.horizontal, .spaceMD)
        .padding(.vertical, .spaceSM)
    }
}
