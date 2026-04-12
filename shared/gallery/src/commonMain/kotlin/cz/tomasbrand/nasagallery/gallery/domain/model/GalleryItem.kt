package cz.tomasbrand.nasagallery.gallery.domain.model

data class GalleryItem(
    val nasaId: String,
    val title: String,
    val description: String?,
    val mediaType: GalleryMediaType,
    val thumbnailUrl: String,
    val dateCreated: String?,
    val photographer: String?,
    val keywords: List<String>,
    val center: String?,
)

enum class GalleryMediaType(val apiValue: String) {
    IMAGE("image"),
    VIDEO("video"),
    AUDIO("audio"),
    OTHER("other");

    companion object {
        fun from(value: String): GalleryMediaType =
            entries.firstOrNull { it.apiValue == value } ?: OTHER
    }
}

data class GalleryPage(
    val items: List<GalleryItem>,
    val totalHits: Int,
    val hasMore: Boolean,
    val nextPage: Int,
)
