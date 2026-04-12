import SwiftUI

// MARK: - NasaChip
// Filter chip — matches Figma Chip (node 11:2637): Default / Selected states.
public struct NasaChip: View {
    private let title: String
    private let isSelected: Bool
    private let action: (() -> Void)?

    public init(title: String, isSelected: Bool = false, action: (() -> Void)? = nil) {
        self.title = title
        self.isSelected = isSelected
        self.action = action
    }

    public var body: some View {
        Group {
            if let action {
                Button(action: action) { chipLabel }
                    .buttonStyle(.plain)
            } else {
                chipLabel
            }
        }
    }

    private var chipLabel: some View {
        Text(title)
            .font(.nasaLabelMedium)
            .foregroundStyle(labelStyle)
            .padding(.horizontal, 17)
            .padding(.vertical, 9)
            .background {
                Capsule()
                    .fill(isSelected ? Color.clear : Color.nasaSurface)
            }
            .overlay(chipBorder)
            .shadow(color: Color.black.opacity(isSelected ? 0 : 0.4), radius: 3, x: 3, y: 3)
            .shadow(color: Color.nasaPrimary.opacity(isSelected ? 0.15 : 0), radius: 7.5, x: 0, y: 0)
            .animation(.easeInOut(duration: 0.2), value: isSelected)
    }

    private var labelStyle: AnyShapeStyle {
        if isSelected {
            return AnyShapeStyle(
                LinearGradient(
                    colors: [Color.nasaAccent, Color.nasaPrimary],
                    startPoint: .leading,
                    endPoint: .trailing
                )
            )
        } else {
            return AnyShapeStyle(Color.nasaChipLabelResting)
        }
    }

    @ViewBuilder
    private var chipBorder: some View {
        Capsule()
            .strokeBorder(
                isSelected ? Color.nasaPrimary.opacity(0.3) : Color.white.opacity(0.03),
                lineWidth: 1
            )
    }

}
