package kmp.shared.auth.data.social

import kmp.shared.auth.domain.model.AuthUser

/**
 * Platform-specific implementation of social sign-in flows.
 * Android: Google via Credential Manager + Firebase Auth
 * iOS: Google via GoogleSignIn SDK + Apple via ASAuthorizationController + Firebase Auth
 */
expect class SocialAuthProvider() {
    suspend fun signInWithGoogle(): SocialAuthResult
    suspend fun signInWithApple(): SocialAuthResult
}

sealed interface SocialAuthResult {
    data class Success(val user: AuthUser) : SocialAuthResult
    data object Cancelled : SocialAuthResult
    data class Error(val message: String, val cause: Throwable? = null) : SocialAuthResult
}
