package kmp.android.shared.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NasaLoadingView(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = MaterialTheme.colors.primary,
            modifier = Modifier.size(40.dp),
        )
    }
}

@Composable
fun NasaFullScreenLoadingView() {
    NasaLoadingView(modifier = Modifier.fillMaxSize())
}

@Composable
fun NasaFooterLoadingView() {
    NasaLoadingView(modifier = Modifier.size(56.dp))
}
