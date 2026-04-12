package cz.tomasbrand.nasagallery.apod.data.remote

import cz.tomasbrand.nasagallery.network.NasaApiConstants
import cz.tomasbrand.nasagallery.network.dto.ApodDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kmp.shared.base.domain.model.Result
import kmp.shared.base.domain.error.util.runCatchingCommonNetworkExceptions

internal class ApodService(
    private val client: HttpClient,
) {
    suspend fun getApod(date: String? = null): Result<ApodDto> =
        runCatchingCommonNetworkExceptions {
            client.get(NasaApiConstants.Endpoints.APOD) {
                if (date != null) parameter(NasaApiConstants.QueryParams.DATE, date)
            }.body()
        }

    suspend fun getApodRange(startDate: String, endDate: String): Result<List<ApodDto>> =
        runCatchingCommonNetworkExceptions {
            client.get(NasaApiConstants.Endpoints.APOD) {
                parameter(NasaApiConstants.QueryParams.START_DATE, startDate)
                parameter(NasaApiConstants.QueryParams.END_DATE, endDate)
            }.body()
        }
}
