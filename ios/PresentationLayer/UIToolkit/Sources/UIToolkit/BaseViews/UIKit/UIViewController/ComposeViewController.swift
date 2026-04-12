import KMPShared
import SwiftUI

public struct ComposeViewController: UIViewControllerRepresentable {
    private let makeScreenViewController: () -> UIViewController
    
    public init(makeScreenViewController: @escaping () -> UIViewController) {
        self.makeScreenViewController = makeScreenViewController
    }
    
    public func makeUIViewController(context: Context) -> UIViewController {
        return makeScreenViewController()
    }
    
    public func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}
