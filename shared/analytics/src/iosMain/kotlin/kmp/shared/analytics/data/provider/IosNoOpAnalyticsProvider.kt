package kmp.shared.analytics.data.provider

import kmp.shared.analytics.domain.model.AnalyticsEvent
import kmp.shared.base.domain.model.Result
import kmp.shared.base.domain.util.extension.success

/**
 * Placeholder until Firebase Analytics (or another SDK) is wired on iOS.
 * Events are accepted so [AnalyticsRepositoryImpl] can be constructed without crashing.
 */
class IosNoOpAnalyticsProvider : AnalyticsProvider {
    override fun logEvent(event: AnalyticsEvent): Result<Unit> = Unit.success()
}
