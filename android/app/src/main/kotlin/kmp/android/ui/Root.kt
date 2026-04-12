package kmp.android.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kmp.android.auth.navigation.AuthGraph
import kmp.android.auth.navigation.authNavGraph
import kmp.shared.auth.presentation.AuthEvent
import kmp.shared.auth.presentation.AuthViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun Root(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = koinViewModel()
    val authState by authViewModel.state.collectAsStateWithLifecycle()

    // Handle sign-out navigation from anywhere in the app
    LaunchedEffect(authViewModel) {
        authViewModel.events.collectLatest { event ->
            if (event is AuthEvent.NavigateToAuth) {
                navController.navigate(AuthGraph.rootPath) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    val startDestination = if (authState.currentUser != null) "main" else AuthGraph.rootPath

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        authNavGraph(
            onNavigateToMain = {
                navController.navigate("main") {
                    popUpTo(AuthGraph.rootPath) { inclusive = true }
                }
            },
            navHostController = navController,
        )
        composable("main") {
            MainScreen()
        }
    }
}
