import SwiftUI

// MARK: - NasaSearchBar
// Search input with clear button and focus state animation.
public struct NasaSearchBar: View {
    @Binding private var text: String
    private let placeholder: String
    private let onSubmit: () -> Void

    @FocusState private var isFocused: Bool

    public init(
        text: Binding<String>,
        placeholder: String = "Search NASA gallery",
        onSubmit: @escaping () -> Void = {}
    ) {
        self._text = text
        self.placeholder = placeholder
        self.onSubmit = onSubmit
    }

    public var body: some View {
        HStack(spacing: .spaceSM) {
            NasaImageAsset.Icon.search
                .renderingMode(.template)
                .resizable()
                .scaledToFit()
                .frame(width: 16, height: 16)
                .foregroundStyle(isFocused ? Color.nasaPrimary : Color.nasaSubtle)
                .animation(.easeInOut(duration: 0.2), value: isFocused)

            TextField(placeholder, text: $text)
                .font(.nasaBodyMedium)
                .foregroundStyle(Color.nasaOnSurface)
                .focused($isFocused)
                .submitLabel(.search)
                .onSubmit(onSubmit)

            if !text.isEmpty {
                Button {
                    text = ""
                    isFocused = false
                } label: {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundStyle(Color.nasaSubtle)
                        .font(.system(size: 16))
                }
                .transition(.scale.combined(with: .opacity))
            }
        }
        .padding(.horizontal, .spaceMD)
        .padding(.vertical, .spaceSM + .spaceXXS)
        .background(Color.nasaSurface)
        .clipShape(Capsule())
        .overlay(
            Capsule()
                .strokeBorder(
                    isFocused ? Color.nasaPrimary : Color.clear,
                    lineWidth: 1.5
                )
                .animation(.easeInOut(duration: 0.2), value: isFocused)
        )
        .animation(.easeInOut(duration: 0.2), value: text.isEmpty)
    }
}
