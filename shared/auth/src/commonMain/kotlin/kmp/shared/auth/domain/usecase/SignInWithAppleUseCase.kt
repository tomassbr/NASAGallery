package kmp.shared.auth.domain.usecase

import kmp.shared.auth.data.social.SocialAuthProvider
import kmp.shared.auth.data.social.SocialAuthResult
import kmp.shared.auth.domain.model.AuthUser
import kmp.shared.base.domain.model.Result
import kmp.shared.base.domain.usecase.UseCaseResultNoParams

interface SignInWithAppleUseCase : UseCaseResultNoParams<AuthUser>

internal class SignInWithAppleUseCaseImpl(
    private val provider: SocialAuthProvider,
) : SignInWithAppleUseCase {
    override suspend fun invoke(): Result<AuthUser> =
        when (val result = provider.signInWithApple()) {
            is SocialAuthResult.Success -> Result.Success(result.user)
            is SocialAuthResult.Cancelled -> Result.Error(AuthError.Cancelled)
            is SocialAuthResult.Error -> Result.Error(AuthError.SignInFailed(result.message))
        }
}
