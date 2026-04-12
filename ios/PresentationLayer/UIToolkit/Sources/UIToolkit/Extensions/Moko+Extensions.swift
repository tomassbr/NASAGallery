import Foundation
import KMPShared

public extension StringResource {
    
    func toLocalized() -> String {
        return self.desc().localized()
    }
}
