import SwiftUI

// MARK: - NasaPageHeader
// Reusable page-level header matching the Figma header pattern (Inter 20 medium + 12 regular subtitle).
// Usage: add via `.safeAreaInset(edge: .top, spacing: 0) { NasaPageHeader(...) { trailingView } }`
//        and hide the system nav bar with `.toolbar(.hidden, for: .navigationBar)`.
public struct NasaPageHeader<Trailing: View>: View {

    private let title: String
    private let subtitle: String?
    @ViewBuilder private let trailing: () -> Trailing

    public init(
        title: String,
        subtitle: String? = nil,
        @ViewBuilder trailing: @escaping () -> Trailing
    ) {
        self.title = title
        self.subtitle = subtitle
        self.trailing = trailing
    }

    public var body: some View {
        HStack(alignment: .center, spacing: .spaceMD) {
            titleStack
                .frame(minWidth: 0, maxWidth: .infinity, alignment: .leading)
                .layoutPriority(1)
                .accessibilityElement(children: .combine)
            trailing()
        }
        .padding(.horizontal, .spaceLG)
        .padding(.top, .spaceMD)
        .padding(.bottom, .spaceMD)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.nasaBackground)
    }

    private var titleStack: some View {
        VStack(alignment: .leading, spacing: 2) {
            Text(title)
                .font(.system(size: 20, weight: .medium))
                .tracking(-0.5)
                .foregroundStyle(Color.nasaOnBackground)
                .lineLimit(1)
                .minimumScaleFactor(0.85)
            if let subtitle {
                Text(subtitle)
                    .font(.system(size: 12, weight: .regular))
                    .foregroundStyle(Color.nasaSubtle)
                    .tracking(1.2)
                    .lineLimit(1)
            }
        }
    }
}

// MARK: - Convenience init without trailing view

public extension NasaPageHeader where Trailing == EmptyView {
    init(title: String, subtitle: String? = nil) {
        self.init(title: title, subtitle: subtitle) { EmptyView() }
    }
}
