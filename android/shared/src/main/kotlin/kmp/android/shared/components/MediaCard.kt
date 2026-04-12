package kmp.android.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import kmp.android.shared.style.NasaColor
import kmp.android.shared.style.Radius
import kmp.android.shared.style.Space

@Composable
fun MediaCard(
    nasaId: String,
    title: String,
    thumbnailUrl: String?,
    isFavorited: Boolean = false,
    onTap: () -> Unit,
    onFavorite: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(Radius.Card))
            .clickable(onClick = onTap),
    ) {
        NasaAsyncImage(
            url = thumbnailUrl,
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        startY = 0.4f,
                    ),
                ),
        )

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.caption,
            color = Color.White,
            maxLines = 2,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(Space.SM),
        )

        // Favorite button
        if (onFavorite != null) {
            FavoriteButton(
                isFavorited = isFavorited,
                onClick = onFavorite,
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
    }
}
