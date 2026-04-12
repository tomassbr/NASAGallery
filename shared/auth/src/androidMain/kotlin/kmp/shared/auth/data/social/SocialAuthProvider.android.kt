package kmp.shared.auth.data.social

import kmp.shared.auth.domain.model.AuthSignInProvider
import kmp.shared.auth.domain.model.AuthUser

/**
 * Android implementation using Credential Manager API (Google One Tap) + Firebase Auth.
 * Apple Sign-In on Android requires web OAuth redirect — implement when needed.
 *
 * TODO: Inject Context and CredentialManager via Koin once Firebase is configured.
 */
actual class SocialAuthProvider actual constructor() {
    actual suspend fun signInWithGoogle(): SocialAuthResult {
        // TODO: Implement with Credential Manager + Firebase Auth
        // 1. val credentialManager = CredentialManager.create(context)
        // 2. val request = GetCredentialRequest.Builder()
        //      .addCredentialOption(GetGoogleIdOption(...))
        //      .build()
        // 3. val result = credentialManager.getCredential(activity, request)
        // 4. val googleIdToken = result.credential.data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID_TOKEN")
        // 5. val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        // 6. val firebaseUser = Firebase.auth.signInWithCredential(firebaseCredential).await().user
        return SocialAuthResult.Error("Not yet implemented — configure Firebase first")
    }

    actual suspend fun signInWithApple(): SocialAuthResult {
        // Apple Sign-In on Android uses web-based OAuth — optional for MVP
        return SocialAuthResult.Error("Apple Sign-In not available on Android")
    }
}
