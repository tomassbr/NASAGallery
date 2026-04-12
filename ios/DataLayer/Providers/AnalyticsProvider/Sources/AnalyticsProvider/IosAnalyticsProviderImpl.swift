import Firebase
import FirebaseAnalytics
import KMPShared

/**
 * iOS implementation of the AnalyticsSource from shared module
 */
public class IosAnalyticsProviderImpl: AnalyticsProvider {
    
    public init() {
        // Start Firebase if not yet started
        if FirebaseApp.app() == nil {
            FirebaseApp.configure()
        }
    }
    
    public func logEvent(event: AnalyticsEvent) -> Result<KotlinUnit> {
        Analytics.logEvent(event.eventName, parameters: event.parameters)
        return ResultSuccess(data: KotlinUnit())
    }
}
