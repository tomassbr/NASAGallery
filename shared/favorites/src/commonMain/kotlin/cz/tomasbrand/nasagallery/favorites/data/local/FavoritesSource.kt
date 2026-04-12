package cz.tomasbrand.nasagallery.favorites.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import cz.tomasbrand.nasagallery.database.FavoriteQueries
import cz.tomasbrand.nasagallery.favorites.domain.model.Favorite
import cz.tomasbrand.nasagallery.gallery.domain.model.GalleryMediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class FavoritesSource(private val queries: FavoriteQueries) {

    fun observeAll(): Flow<List<Favorite>> =
        queries.selectAllFavorites()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { it.toFavorite() } }

    fun isFavorite(nasaId: String): Boolean =
        queries.isFavorite(nasaId).executeAsOne()

    fun add(favorite: Favorite) {
        queries.insertFavorite(
            id = favorite.nasaId,
            nasa_id = favorite.nasaId,
            title = favorite.title,
            description = favorite.description,
            media_type = favorite.mediaType.apiValue,
            thumbnail_url = favorite.thumbnailUrl,
            full_url = favorite.fullUrl,
            date_created = favorite.dateCreated,
            saved_at = Clock.System.now().toEpochMilliseconds(),
        )
    }

    fun remove(nasaId: String) {
        queries.deleteFavorite(nasaId)
    }
}

private fun cz.tomasbrand.nasagallery.database.Favorite.toFavorite() = Favorite(
    nasaId = nasa_id,
    title = title,
    description = description,
    mediaType = GalleryMediaType.from(media_type),
    thumbnailUrl = thumbnail_url,
    fullUrl = full_url,
    dateCreated = date_created,
    savedAt = saved_at,
)
