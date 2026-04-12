package cz.tomasbrand.nasagallery.gallery.data.remote

import cz.tomasbrand.nasagallery.network.NasaApiConstants
import cz.tomasbrand.nasagallery.network.dto.GalleryResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kmp.shared.base.domain.model.Result
import kmp.shared.base.domain.error.util.runCatchingCommonNetworkExceptions

internal class GalleryService(private val client: HttpClient) {

    suspend fun search(
        query: String,
        mediaType: String? = "image",
        page: Int = 1,
        pageSize: Int = NasaApiConstants.IMAGES_PAGE_SIZE,
    ): Result<GalleryResponseDto> = runCatchingCommonNetworkExceptions {
        client.get(NasaApiConstants.Endpoints.IMAGES_SEARCH) {
            parameter(NasaApiConstants.QueryParams.QUERY, query)
            if (mediaType != null) parameter(NasaApiConstants.QueryParams.MEDIA_TYPE, mediaType)
            parameter(NasaApiConstants.QueryParams.PAGE, page)
            parameter(NasaApiConstants.QueryParams.PAGE_SIZE, pageSize)
        }.body()
    }

    suspend fun getFeatured(page: Int = 1): Result<GalleryResponseDto> =
        search(query = "space", mediaType = "image", page = page)
}
