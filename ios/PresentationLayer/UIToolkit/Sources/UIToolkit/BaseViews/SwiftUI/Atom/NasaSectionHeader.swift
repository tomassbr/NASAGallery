import SwiftUI

// MARK: - NasaSectionHeader
// Overline-style section title with uppercase tracking — used above grouped settings rows.
public struct NasaSectionHeader: View {
    private let title: String

    public init(_ title: String) {
        self.title = title
    }

    public var body: some View {
        Text(title)
            .font(.nasaLabelSmall)
            .foregroundStyle(Color.nasaSubtle)
            .tracking(1.5)
            .textCase(nil)
    }
}
