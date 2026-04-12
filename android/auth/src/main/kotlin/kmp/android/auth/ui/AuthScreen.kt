package kmp.android.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kmp.android.shared.style.NasaColor
import kmp.android.shared.style.Radius
import kmp.android.shared.style.Space
import kmp.shared.auth.presentation.AuthEvent
import kmp.shared.auth.presentation.AuthIntent
import kmp.shared.auth.presentation.AuthState
import kmp.shared.auth.presentation.AuthViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AuthRoute(onNavigateToMain: () -> Unit) {
    val viewModel: AuthViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { event ->
            when (event) {
                AuthEvent.NavigateToMain -> onNavigateToMain()
                else -> Unit
            }
        }
    }

    AuthScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun AuthScreen(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NasaColor.Background),
    ) {
        // Space background — nebula blobs
        SpaceBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Space.screenHorizontal),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1f))

            // Logo
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(NasaColor.Primary, NasaColor.Primary.copy(alpha = 0.7f)),
                        ),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp),
                )
            }

            Spacer(Modifier.height(Space.MD))

            Text(
                text = "NASA Gallery",
                style = MaterialTheme.typography.h4,
                color = NasaColor.OnBackground,
            )

            Spacer(Modifier.height(Space.SM))

            Text(
                text = "Explore the universe, one image at a time",
                style = MaterialTheme.typography.body2,
                color = NasaColor.OnBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.weight(1f))

            // Google sign-in button
            Button(
                onClick = { onIntent(AuthIntent.SignInWithGoogle) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(Radius.MD),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Space.SM),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "G",
                        style = MaterialTheme.typography.h6,
                        color = Color(0xFF4285F4),
                    )
                    Text(
                        text = "Continue with Google",
                        style = MaterialTheme.typography.button,
                        color = Color(0xFF111827),
                    )
                }
            }

            Spacer(Modifier.height(Space.SM))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Space.MD),
            ) {
                Box(Modifier.weight(1f).height(1.dp).background(Color.White.copy(alpha = 0.15f)))
                Text("or", style = MaterialTheme.typography.overline, color = Color.White.copy(alpha = 0.4f))
                Box(Modifier.weight(1f).height(1.dp).background(Color.White.copy(alpha = 0.15f)))
            }

            Spacer(Modifier.height(Space.SM))

            // Guest button
            TextButton(
                onClick = { onIntent(AuthIntent.SignInAsGuest) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
            ) {
                Text(
                    text = "Continue as Guest",
                    style = MaterialTheme.typography.body1,
                    color = NasaColor.Accent,
                )
            }

            Spacer(Modifier.height(Space.SM))

            Text(
                text = "Guests can browse images but cannot save favorites.",
                style = MaterialTheme.typography.overline,
                color = Color.White.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(Space.XXXL))
        }

        // Loading overlay
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = NasaColor.Primary)
            }
        }
    }

    // Error dialog
    if (state.error != null) {
        AlertDialog(
            onDismissRequest = { onIntent(AuthIntent.DismissError) },
            title = { Text("Sign In Failed") },
            text = { Text(state.error?.throwable?.message ?: "An error occurred") },
            confirmButton = {
                TextButton(onClick = { onIntent(AuthIntent.DismissError) }) {
                    Text("OK")
                }
            },
            backgroundColor = NasaColor.Surface,
            contentColor = NasaColor.OnSurface,
        )
    }
}

@Composable
private fun SpaceBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top-left nebula
        Box(
            modifier = Modifier
                .size(320.dp, 260.dp)
                .background(
                    color = Color(0xFF0F2B6E).copy(alpha = 0.7f),
                    shape = CircleShape,
                )
                .blur(90.dp),
        )
        // Center-right nebula
        Box(
            modifier = Modifier
                .size(240.dp, 180.dp)
                .align(Alignment.CenterEnd)
                .background(
                    color = Color(0xFF0A1F5C).copy(alpha = 0.5f),
                    shape = CircleShape,
                )
                .blur(80.dp),
        )
    }
}
