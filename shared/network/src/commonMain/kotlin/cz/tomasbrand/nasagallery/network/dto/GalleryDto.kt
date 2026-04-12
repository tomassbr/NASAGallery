package cz.tomasbrand.nasagallery.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GalleryResponseDto(
    val collection: CollectionDto,
)

@Serializable
data class CollectionDto(
    val items: List<GalleryItemDto> = emptyList(),
    val links: List<GalleryLinkDto>? = null,
    val metadata: MetadataDto,
)

@Serializable
data class MetadataDto(
    @SerialName("total_hits") val totalHits: Int = 0,
)

@Serializable
data class GalleryItemDto(
    val data: List<GalleryDataDto> = emptyList(),
    val links: List<GalleryLinkDto>? = null,
)

@Serializable
data class GalleryDataDto(
    @SerialName("nasa_id") val nasaId: String,
    val title: String,
    val description: String? = null,
    @SerialName("media_type") val mediaType: String,
    @SerialName("date_created") val dateCreated: String? = null,
    val photographer: String? = null,
    val keywords: List<String>? = null,
    val center: String? = null,
)

@Serializable
data class GalleryLinkDto(
    val href: String,
    val rel: String,         // "preview" | "captions" | "next" | "prev"
    val render: String? = null,
    val prompt: String? = null,
)

@Serializable
data class AssetCollectionDto(
    val collection: AssetItemsDto,
)

@Serializable
data class AssetItemsDto(
    val items: List<AssetItemDto> = emptyList(),
)

@Serializable
data class AssetItemDto(
    val href: String,
)
