package cz.tomasbrand.nasagallery.profile.device

import android.content.Context
import coil3.SingletonImageLoader
import cz.tomasbrand.nasagallery.profile.domain.ImageCacheClearer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidImageCacheClearer(
    private val context: Context,
) : ImageCacheClearer {
    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            val loader = SingletonImageLoader.get(context)
            loader.memoryCache?.clear()
            loader.diskCache?.clear()
        }
    }
}
