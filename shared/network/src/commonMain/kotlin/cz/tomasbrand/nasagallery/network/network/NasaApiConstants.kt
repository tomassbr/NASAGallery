package cz.tomasbrand.nasagallery.network

object NasaApiConstants {
    const val APOD_BASE_URL = "api.nasa.gov"
    const val IMAGES_BASE_URL = "images-api.nasa.gov"

    object Endpoints {
        const val APOD = "/planetary/apod"
        const val IMAGES_SEARCH = "/search"
        const val IMAGES_ASSET = "/asset"
        const val IMAGES_METADATA = "/metadata"
    }

    object QueryParams {
        const val API_KEY = "api_key"
        const val DATE = "date"
        const val START_DATE = "start_date"
        const val END_DATE = "end_date"
        const val COUNT = "count"
        const val QUERY = "q"
        const val MEDIA_TYPE = "media_type"
        const val PAGE = "page"
        const val PAGE_SIZE = "page_size"
    }

    const val IMAGES_PAGE_SIZE = 20
}
