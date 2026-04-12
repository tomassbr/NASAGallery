package cz.tomasbrand.nasagallery.favorites.domain.mapping

import cz.tomasbrand.nasagallery.favorites.domain.model.Favorite
import cz.tomasbrand.nasagallery.gallery.domain.model.GalleryItem

fun GalleryItem.toFavorite(): Favorite = Favorite(
    nasaId = nasaId,
    title = title,
    description = description,
    mediaType = mediaType,
    thumbnailUrl = thumbnailUrl,
    fullUrl = null,
    dateCreated = dateCreated,
    savedAt = 0L,
)
