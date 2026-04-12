package cz.tomasbrand.nasagallery.favorites.domain.model

import cz.tomasbrand.nasagallery.gallery.domain.model.GalleryMediaType

data class Favorite(
    val nasaId: String,
    val title: String,
    val description: String?,
    val mediaType: GalleryMediaType,
    val thumbnailUrl: String,
    val fullUrl: String?,
    val dateCreated: String?,
    val savedAt: Long,
)
