package kmp.shared.auth.domain.usecase

import dev.icerock.moko.resources.desc.desc
import kmp.shared.base.domain.model.ErrorResult

sealed class AuthError(message: String, throwable: Throwable? = null) : ErrorResult(
    localizedMessage = message.desc(),
    throwable = throwable,
) {
    data object Cancelled : AuthError("Sign-in was cancelled")
    data class SignInFailed(val reason: String) : AuthError(reason)
    data object NotAuthenticated : AuthError("User is not authenticated")
    data object GuestNotAllowed : AuthError("This action requires sign-in")
}
