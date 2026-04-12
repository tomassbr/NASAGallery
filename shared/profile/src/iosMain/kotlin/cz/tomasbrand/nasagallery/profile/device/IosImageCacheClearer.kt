package cz.tomasbrand.nasagallery.profile.device

import cz.tomasbrand.nasagallery.profile.domain.ImageCacheClearer
import platform.Foundation.NSURLCache

class IosImageCacheClearer : ImageCacheClearer {
    override suspend fun clear() {
        NSURLCache.sharedURLCache.removeAllCachedResponses()
    }
}
