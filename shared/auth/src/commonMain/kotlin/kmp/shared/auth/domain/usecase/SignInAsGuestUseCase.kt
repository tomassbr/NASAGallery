package kmp.shared.auth.domain.usecase

import kmp.shared.auth.domain.model.AuthSignInProvider
import kmp.shared.auth.domain.model.AuthUser
import kmp.shared.base.domain.model.Result
import kmp.shared.base.domain.usecase.UseCaseResultNoParams

interface SignInAsGuestUseCase : UseCaseResultNoParams<AuthUser>

internal class SignInAsGuestUseCaseImpl(
    private val sessionStore: GuestSessionStore,
) : SignInAsGuestUseCase {
    override suspend fun invoke(): Result<AuthUser> {
        val guestUser = AuthUser(
            uid = "guest",
            email = null,
            displayName = null,
            photoUrl = null,
            provider = AuthSignInProvider.GUEST,
            isGuest = true,
        )
        sessionStore.saveGuestSession(guestUser)
        return Result.Success(guestUser)
    }
}
