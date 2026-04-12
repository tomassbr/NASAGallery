package kmp.shared.auth.domain.usecase

import kmp.shared.auth.domain.model.AuthUser
import kmp.shared.auth.domain.model.AuthSignInProvider
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val KEY_IS_GUEST = "is_guest_session"
private const val KEY_AUTH_UID = "auth_uid"
private const val KEY_AUTH_PROVIDER = "auth_provider"

/**
 * Lightweight session store backed by multiplatform-settings.
 * Stores whether the user is signed in as guest, or persists the Firebase UID.
 */
class GuestSessionStore(private val settings: Settings) {

    private val _currentUser = MutableStateFlow<AuthUser?>(loadFromPrefs())

    val currentUserFlow: Flow<AuthUser?> = _currentUser.asStateFlow()
    val currentUser: AuthUser? get() = _currentUser.value

    fun saveGuestSession(user: AuthUser) {
        settings.putBoolean(KEY_IS_GUEST, true)
        settings.putString(KEY_AUTH_UID, user.uid)
        settings.putString(KEY_AUTH_PROVIDER, user.provider.name)
        _currentUser.value = user
    }

    fun saveSignedInUser(user: AuthUser) {
        settings.putBoolean(KEY_IS_GUEST, false)
        settings.putString(KEY_AUTH_UID, user.uid)
        settings.putString(KEY_AUTH_PROVIDER, user.provider.name)
        _currentUser.value = user
    }

    fun clearSession() {
        settings.remove(KEY_IS_GUEST)
        settings.remove(KEY_AUTH_UID)
        settings.remove(KEY_AUTH_PROVIDER)
        _currentUser.value = null
    }

    private fun loadFromPrefs(): AuthUser? {
        val uid = settings.getStringOrNull(KEY_AUTH_UID) ?: return null
        val isGuest = settings.getBoolean(KEY_IS_GUEST, false)
        val providerName = settings.getStringOrNull(KEY_AUTH_PROVIDER)
        val provider = providerName?.let {
            runCatching { AuthSignInProvider.valueOf(it) }.getOrNull()
        } ?: if (isGuest) AuthSignInProvider.GUEST else return null

        return AuthUser(
            uid = uid,
            email = null,
            displayName = null,
            photoUrl = null,
            provider = provider,
            isGuest = isGuest,
        )
    }
}
