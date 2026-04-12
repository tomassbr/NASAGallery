package cz.tomasbrand.nasagallery.gallery.data.repository

import cz.tomasbrand.nasagallery.gallery.data.remote.GalleryService
import cz.tomasbrand.nasagallery.gallery.domain.model.GalleryItem
import cz.tomasbrand.nasagallery.gallery.domain.model.GalleryMediaType
import cz.tomasbrand.nasagallery.gallery.domain.model.GalleryPage
import cz.tomasbrand.nasagallery.network.NasaApiConstants
import cz.tomasbrand.nasagallery.network.dto.GalleryItemDto
import cz.tomasbrand.nasagallery.network.dto.GalleryResponseDto
import kmp.shared.base.domain.model.Result

interface GalleryRepository {
    suspend fun getPage(page: Int): Result<GalleryPage>
    suspend fun searchPage(query: String, mediaType: String?, page: Int): Result<GalleryPage>
}

internal class GalleryRepositoryImpl(
    private val service: GalleryService,
) : GalleryRepository {

    override suspend fun getPage(page: Int): Result<GalleryPage> =
        service.getFeatured(page).map { it.toPage(page) }

    override suspend fun searchPage(
        query: String,
        mediaType: String?,
        page: Int,
    ): Result<GalleryPage> = service.search(query, mediaType, page).map { it.toPage(page) }
}

private fun <T : Any, R : Any> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> Result.Error(error)
}

private fun GalleryResponseDto.toPage(currentPage: Int): GalleryPage {
    val items = collection.items.mapNotNull { it.toGalleryItem() }
    val totalHits = collection.metadata.totalHits
    val hasMore = currentPage * NasaApiConstants.IMAGES_PAGE_SIZE < totalHits
    return GalleryPage(
        items = items,
        totalHits = totalHits,
        hasMore = hasMore,
        nextPage = currentPage + 1,
    )
}

private fun GalleryItemDto.toGalleryItem(): GalleryItem? {
    val data = data.firstOrNull() ?: return null
    val thumbnailUrl = links?.firstOrNull { it.rel == "preview" }?.href ?: return null
    return GalleryItem(
        nasaId = data.nasaId,
        title = data.title,
        description = data.description,
        mediaType = GalleryMediaType.from(data.mediaType),
        thumbnailUrl = thumbnailUrl,
        dateCreated = data.dateCreated,
        photographer = data.photographer,
        keywords = data.keywords ?: emptyList(),
        center = data.center,
    )
}
