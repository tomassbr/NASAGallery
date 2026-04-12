package cz.tomasbrand.nasagallery.profile.domain

/**
 * Clears image / HTTP response caches used for thumbnails and full-screen media.
 */
interface ImageCacheClearer {
    suspend fun clear()
}
