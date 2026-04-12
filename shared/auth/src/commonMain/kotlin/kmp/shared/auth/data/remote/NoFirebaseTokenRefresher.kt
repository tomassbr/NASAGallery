package kmp.shared.auth.data.remote

/**
 * Placeholder until Firebase Auth issues refreshable tokens.
 * Returning null avoids persisting a fake token when no backend is configured.
 */
internal class NoFirebaseTokenRefresher : TokenRefresher {
    override suspend fun refresh(): String? = null
}
