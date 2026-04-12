import KMPShared
import Utilities

public class ConfigImpl: Config {
    
    public init() {}

    public var apiVariant: KMPShared.ApiVariant {
        switch Environment.api {
        case .alpha: KMPShared.ApiVariant.alpha
        case .production: KMPShared.ApiVariant.production
        }
    }
    
    public var isRelease: Bool {
        Environment.build == .release
    }
}
