package cz.tomasbrand.nasagallery.apod.domain.model

data class Apod(
    val date: String,          // YYYY-MM-DD
    val title: String,
    val explanation: String,
    val url: String,
    val hdUrl: String?,
    val mediaType: MediaType,
    val copyright: String?,
    val thumbnailUrl: String?, // Only for videos
) {
    val displayUrl: String get() = if (mediaType == MediaType.VIDEO) thumbnailUrl ?: url else url
    val isVideo: Boolean get() = mediaType == MediaType.VIDEO
}

enum class MediaType(val apiValue: String) {
    IMAGE("image"),
    VIDEO("video"),
    OTHER("other");

    companion object {
        fun from(value: String): MediaType =
            entries.firstOrNull { it.apiValue == value } ?: OTHER
    }
}
