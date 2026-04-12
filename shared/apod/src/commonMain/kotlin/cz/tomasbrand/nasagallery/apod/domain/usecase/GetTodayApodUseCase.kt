package cz.tomasbrand.nasagallery.apod.domain.usecase

import cz.tomasbrand.nasagallery.apod.data.repository.ApodRepository
import cz.tomasbrand.nasagallery.apod.domain.model.Apod
import kmp.shared.base.domain.model.Result
import kmp.shared.base.domain.usecase.UseCaseResultNoParams

interface GetTodayApodUseCase : UseCaseResultNoParams<Apod>

internal class GetTodayApodUseCaseImpl(
    private val repository: ApodRepository,
) : GetTodayApodUseCase {
    override suspend fun invoke(): Result<Apod> = repository.getToday()
}
