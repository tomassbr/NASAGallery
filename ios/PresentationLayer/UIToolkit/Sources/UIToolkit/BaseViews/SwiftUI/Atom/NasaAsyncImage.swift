import SDWebImageSwiftUI
import SwiftUI

// MARK: - NasaAsyncImage
// Async image with shimmer placeholder and error fallback.
// Uses SDWebImage for caching and progressive loading.
public struct NasaAsyncImage: View {
    private let url: URL?
    private let contentMode: ContentMode
    private let cornerRadius: CGFloat

    public init(
        url: URL?,
        contentMode: ContentMode = .fill,
        cornerRadius: CGFloat = 0
    ) {
        self.url = url
        self.contentMode = contentMode
        self.cornerRadius = cornerRadius
    }

    public var body: some View {
        WebImage(url: url) { image in
            image
                .resizable()
                .aspectRatio(contentMode: contentMode)
        } placeholder: {
            NasaShimmerView()
        }
        .indicator(.activity)
        .transition(.opacity.animation(.easeInOut(duration: 0.3)))
        .clipShape(RoundedRectangle(cornerRadius: cornerRadius, style: .continuous))
    }
}

// MARK: - NasaShimmerView
public struct NasaShimmerView: View {
    @State private var phase: CGFloat = 0

    public init() {}

    public var body: some View {
        GeometryReader { geo in
            let gradient = LinearGradient(
                colors: [
                    Color.nasaSurface,
                    Color.nasaSurfaceElevated,
                    Color.nasaSurface
                ],
                startPoint: UnitPoint(x: phase - 0.5, y: 0),
                endPoint: UnitPoint(x: phase + 0.5, y: 0)
            )
            Rectangle()
                .fill(gradient)
                .frame(width: geo.size.width, height: geo.size.height)
        }
        .onAppear {
            withAnimation(.linear(duration: 1.2).repeatForever(autoreverses: false)) {
                phase = 1
            }
        }
    }
}
