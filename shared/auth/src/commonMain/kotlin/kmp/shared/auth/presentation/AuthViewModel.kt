package kmp.shared.auth.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kmp.shared.auth.domain.model.AuthUser
import kmp.shared.auth.domain.usecase.GetCurrentUserUseCase
import kmp.shared.auth.domain.usecase.SignInAsGuestUseCase
import kmp.shared.auth.domain.usecase.SignInWithAppleUseCase
import kmp.shared.auth.domain.usecase.SignInWithGoogleUseCase
import kmp.shared.auth.domain.usecase.SignOutUseCase
import kmp.shared.base.domain.model.ErrorResult
import kmp.shared.base.domain.util.extension.alsoOnError
import kmp.shared.base.domain.util.extension.alsoOnSuccess
import kmp.shared.base.presentation.vm.BaseScopedViewModel
import kmp.shared.base.presentation.vm.VmEvent
import kmp.shared.base.presentation.vm.VmIntent
import kmp.shared.base.presentation.vm.VmState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AuthViewModel(
    private val signInWithGoogle: SignInWithGoogleUseCase,
    private val signInWithApple: SignInWithAppleUseCase,
    private val signInAsGuest: SignInAsGuestUseCase,
    private val signOut: SignOutUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
) : BaseScopedViewModel<AuthState, AuthIntent, AuthEvent>() {

    private var currentUser: AuthUser? by mutableStateOf(null)
    private var isLoading: Boolean by mutableStateOf(false)
    private var error: ErrorResult? by mutableStateOf(null)

    init {
        getCurrentUser()
            .onEach { user -> currentUser = user }
            .launchIn(viewModelScope)
    }

    @Composable
    override fun getState() = AuthState(
        currentUser = currentUser,
        isLoading = isLoading,
        isGuest = currentUser?.isGuest == true,
        error = error,
    )

    override fun onViewAppeared() {
        // No-op: auth state is observed reactively via GetCurrentUserUseCase in init
    }

    override fun onIntent(intent: AuthIntent) {
        viewModelScope.launch {
            when (intent) {
                AuthIntent.SignInWithGoogle -> handleGoogleSignIn()
                AuthIntent.SignInWithApple -> handleAppleSignIn()
                AuthIntent.SignInAsGuest -> handleGuestSignIn()
                AuthIntent.SignOut -> handleSignOut()
                AuthIntent.DismissError -> error = null
            }
        }
    }

    private suspend fun handleGoogleSignIn() {
        isLoading = true
        error = null
        signInWithGoogle()
            .alsoOnSuccess { user ->
                isLoading = false
                _events.emit(AuthEvent.NavigateToMain)
            }
            .alsoOnError { err ->
                isLoading = false
                error = err
            }
    }

    private suspend fun handleAppleSignIn() {
        isLoading = true
        error = null
        signInWithApple()
            .alsoOnSuccess {
                isLoading = false
                _events.emit(AuthEvent.NavigateToMain)
            }
            .alsoOnError { err ->
                isLoading = false
                error = err
            }
    }

    private suspend fun handleGuestSignIn() {
        isLoading = true
        signInAsGuest()
            .alsoOnSuccess {
                isLoading = false
                _events.emit(AuthEvent.NavigateToMain)
            }
            .alsoOnError { err ->
                isLoading = false
                error = err
            }
    }

    private suspend fun handleSignOut() {
        signOut()
        _events.emit(AuthEvent.NavigateToAuth)
    }
}

data class AuthState(
    val currentUser: AuthUser? = null,
    val isLoading: Boolean = false,
    val isGuest: Boolean = false,
    val error: ErrorResult? = null,
) : VmState

sealed interface AuthIntent : VmIntent {
    data object SignInWithGoogle : AuthIntent
    data object SignInWithApple : AuthIntent
    data object SignInAsGuest : AuthIntent
    data object SignOut : AuthIntent
    data object DismissError : AuthIntent
}

sealed interface AuthEvent : VmEvent {
    data object NavigateToMain : AuthEvent
    data object NavigateToAuth : AuthEvent
    data object ShowSignInSheet : AuthEvent
}
