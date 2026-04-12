package kmp.shared.auth.domain.usecase

import kmp.shared.auth.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface GetCurrentUserUseCase {
    operator fun invoke(): Flow<AuthUser?>
}

internal class GetCurrentUserUseCaseImpl(
    private val sessionStore: GuestSessionStore,
) : GetCurrentUserUseCase {
    override fun invoke(): Flow<AuthUser?> = sessionStore.currentUserFlow
}
