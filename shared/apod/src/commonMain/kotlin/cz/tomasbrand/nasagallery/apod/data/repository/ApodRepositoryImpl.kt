package cz.tomasbrand.nasagallery.apod.data.repository

import cz.tomasbrand.nasagallery.apod.data.local.ApodCacheSource
import cz.tomasbrand.nasagallery.apod.data.remote.ApodService
import cz.tomasbrand.nasagallery.apod.domain.model.Apod
import cz.tomasbrand.nasagallery.apod.domain.model.MediaType
import cz.tomasbrand.nasagallery.network.dto.ApodDto
import kmp.shared.base.domain.model.Result
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

internal interface ApodRepository {
    suspend fun getToday(): Result<Apod>
    suspend fun getForDate(date: String): Result<Apod>
    suspend fun getRange(startDate: String, endDate: String): Result<List<Apod>>
}

internal class ApodRepositoryImpl(
    private val service: ApodService,
    private val cache: ApodCacheSource,
) : ApodRepository {

    override suspend fun getToday(): Result<Apod> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        cache.getByDate(today)?.let { return Result.Success(it) }
        return service.getApod(date = null).map { dto ->
            dto.toApod().also { cache.save(it) }
        }
    }

    override suspend fun getForDate(date: String): Result<Apod> {
        cache.getByDate(date)?.let { return Result.Success(it) }
        return service.getApod(date = date).map { dto ->
            dto.toApod().also { cache.save(it) }
        }
    }

    override suspend fun getRange(startDate: String, endDate: String): Result<List<Apod>> =
        service.getApodRange(startDate, endDate).map { dtos -> dtos.map { it.toApod() } }
}

private fun <T : Any, R : Any> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> Result.Error(error, data?.let { null })
}

private fun ApodDto.toApod() = Apod(
    date = date,
    title = title,
    explanation = explanation,
    url = url,
    hdUrl = hdUrl,
    mediaType = MediaType.from(mediaType),
    copyright = copyright,
    thumbnailUrl = thumbnailUrl,
)
