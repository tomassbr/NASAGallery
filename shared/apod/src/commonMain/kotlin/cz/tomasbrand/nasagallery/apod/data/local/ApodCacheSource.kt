package cz.tomasbrand.nasagallery.apod.data.local

import cz.tomasbrand.nasagallery.apod.domain.model.Apod
import cz.tomasbrand.nasagallery.apod.domain.model.MediaType
import cz.tomasbrand.nasagallery.database.ApodCacheQueries
import kotlinx.datetime.Clock

internal class ApodCacheSource(private val queries: ApodCacheQueries) {

    fun getByDate(date: String): Apod? =
        queries.selectApodByDate(date).executeAsOneOrNull()?.toApod()

    fun getLatest(): Apod? =
        queries.selectLatestApod().executeAsOneOrNull()?.toApod()

    fun save(apod: Apod) {
        queries.insertApodCache(
            date = apod.date,
            title = apod.title,
            explanation = apod.explanation,
            url = apod.url,
            hd_url = apod.hdUrl,
            media_type = apod.mediaType.apiValue,
            copyright = apod.copyright,
            thumbnail_url = apod.thumbnailUrl,
            cached_at = Clock.System.now().toEpochMilliseconds(),
        )
    }

    fun evictOlderThan(epochMillis: Long) {
        queries.deleteApodOlderThan(epochMillis)
    }
}

private fun cz.tomasbrand.nasagallery.database.ApodCache.toApod() = Apod(
    date = date,
    title = title,
    explanation = explanation,
    url = url,
    hdUrl = hd_url,
    mediaType = MediaType.from(media_type),
    copyright = copyright,
    thumbnailUrl = thumbnail_url,
)
