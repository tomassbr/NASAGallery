package kmp.shared.auth.domain.model

data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val provider: AuthSignInProvider,
    val isGuest: Boolean,
)

enum class AuthSignInProvider {
    GOOGLE,
    APPLE,
    GUEST,
}
