package kmp.shared.auth.data.social

import kmp.shared.auth.domain.model.AuthSignInProvider
import kmp.shared.auth.domain.model.AuthUser

/**
 * iOS implementation:
 * - Google: GoogleSignIn SDK → Firebase Auth
 * - Apple: ASAuthorizationAppleIDProvider → Firebase Auth
 *
 * Both flows are bridged to Kotlin via SKIE-exported async callbacks.
 * Platform coordinators live in ios/NasaGallery/Auth/ (Swift).
 *
 * TODO: Inject via KoinHelper once Firebase iOS SDK is configured.
 */
actual class SocialAuthProvider actual constructor() {
    actual suspend fun signInWithGoogle(): SocialAuthResult {
        // TODO: Implement via Swift-bridged GoogleSignInCoordinator
        // The Swift coordinator calls GIDSignIn.sharedInstance.signIn(withPresenting:)
        // then exchanges the idToken with Firebase Auth
        return SocialAuthResult.Error("Not yet implemented — configure Firebase + GoogleSignIn first")
    }

    actual suspend fun signInWithApple(): SocialAuthResult {
        // TODO: Implement via Swift-bridged AppleSignInCoordinator
        // Uses ASAuthorizationAppleIDProvider + ASAuthorizationController
        // Then exchanges credential with Firebase Auth
        return SocialAuthResult.Error("Not yet implemented — configure Firebase + Apple Sign-In first")
    }
}
