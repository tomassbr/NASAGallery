import SwiftUI

// MARK: - NasaLoadingView
// Circular progress indicator using NASA primary color.
public struct NasaLoadingView: View {
    private let size: CGFloat

    public init(size: CGFloat = 32) {
        self.size = size
    }

    public var body: some View {
        ProgressView()
            .progressViewStyle(.circular)
            .tint(.nasaPrimary)
            .frame(width: size, height: size)
    }
}

// MARK: - NasaFullScreenLoadingView
// Full-screen loading overlay.
public struct NasaFullScreenLoadingView: View {
    public init() {}

    public var body: some View {
        ZStack {
            Color.nasaBackground.ignoresSafeArea()
            NasaLoadingView(size: 48)
        }
    }
}

// MARK: - NasaLoadingFooter
// Loading indicator for pagination (at the bottom of a list/grid).
public struct NasaLoadingFooter: View {
    public init() {}

    public var body: some View {
        HStack {
            Spacer()
            NasaLoadingView(size: 24)
            Spacer()
        }
        .frame(height: 56)
    }
}
