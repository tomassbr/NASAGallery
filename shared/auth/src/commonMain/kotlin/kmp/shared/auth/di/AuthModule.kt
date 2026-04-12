package kmp.shared.auth.di

import kmp.shared.auth.data.remote.NoFirebaseTokenRefresher
import kmp.shared.auth.data.remote.TokenRefresher
import kmp.shared.auth.data.social.SocialAuthProvider
import kmp.shared.auth.domain.usecase.GetCurrentUserUseCase
import kmp.shared.auth.domain.usecase.GetCurrentUserUseCaseImpl
import kmp.shared.auth.domain.usecase.GuestSessionStore
import kmp.shared.auth.domain.usecase.SignInAsGuestUseCase
import kmp.shared.auth.domain.usecase.SignInAsGuestUseCaseImpl
import kmp.shared.auth.domain.usecase.SignInWithAppleUseCase
import kmp.shared.auth.domain.usecase.SignInWithAppleUseCaseImpl
import kmp.shared.auth.domain.usecase.SignInWithGoogleUseCase
import kmp.shared.auth.domain.usecase.SignInWithGoogleUseCaseImpl
import kmp.shared.auth.domain.usecase.SignOutUseCase
import kmp.shared.auth.domain.usecase.SignOutUseCaseImpl
import kmp.shared.auth.presentation.AuthViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {
    includes(authPlatformModule)

    // Token refresher (no-op until Firebase Auth refresh is wired)
    singleOf(::NoFirebaseTokenRefresher) bind TokenRefresher::class

    // Session store
    singleOf(::GuestSessionStore)

    // Social auth provider (platform actual)
    singleOf(::SocialAuthProvider)

    // Use cases
    singleOf(::SignInWithGoogleUseCaseImpl) bind SignInWithGoogleUseCase::class
    singleOf(::SignInWithAppleUseCaseImpl) bind SignInWithAppleUseCase::class
    singleOf(::SignInAsGuestUseCaseImpl) bind SignInAsGuestUseCase::class
    singleOf(::SignOutUseCaseImpl) bind SignOutUseCase::class
    singleOf(::GetCurrentUserUseCaseImpl) bind GetCurrentUserUseCase::class

    // ViewModel
    viewModelOf(::AuthViewModel)
}

internal expect val authPlatformModule: Module
