package cz.tomasbrand.nasagallery.apod.domain.usecase

import cz.tomasbrand.nasagallery.apod.data.repository.ApodRepository
import cz.tomasbrand.nasagallery.apod.domain.model.Apod
import kmp.shared.base.domain.model.Result
import kmp.shared.base.domain.usecase.UseCaseResult

interface GetApodForDateUseCase : UseCaseResult<String, Apod>

internal class GetApodForDateUseCaseImpl(
    private val repository: ApodRepository,
) : GetApodForDateUseCase {
    override suspend fun invoke(params: String): Result<Apod> = repository.getForDate(params)
}
