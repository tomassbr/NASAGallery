package kmp.shared.auth.domain.usecase

import kmp.shared.base.domain.usecase.UseCaseResultNoParams
import kmp.shared.base.domain.model.Result

interface SignOutUseCase : UseCaseResultNoParams<Unit>

internal class SignOutUseCaseImpl(
    private val sessionStore: GuestSessionStore,
) : SignOutUseCase {
    override suspend fun invoke(): Result<Unit> {
        sessionStore.clearSession()
        return Result.Success(Unit)
    }
}
