package kmp.android.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Search
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
fun ApodHeroCard(
    title: String,
    imageUrl: String?,
    date: String,
    explanation: String,
    copyright: String?,
    isFavorited: Boolean = false,
    onFavorite: (() -> Unit)? = null,
    onShare: () -> Unit,
    onViewFullscreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .clip(RoundedCornerShape(Radius.Card)),
    ) {
        NasaAsyncImage(
            url = imageUrl,
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // Gradient overlay — transparent at top, dark at bottom (starts fading at ~40%)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.4f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.85f),
                    ),
                ),
        )

        // Top-left APOD badge
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(Space.MD),
        ) {
            ApodBadge(date = date)
        }

        // Bottom labels stack
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(Space.MD),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle1,
                color = Color.White,
                maxLines = 2,
            )
            Text(
                text = explanation,
                style = MaterialTheme.typography.caption,
                color = Color.White.copy(alpha = 0.75f),
                maxLines = 2,
            )
            if (copyright != null) {
                Text(
                    text = "© $copyright",
                    style = MaterialTheme.typography.caption,
                    color = Color.White.copy(alpha = 0.5f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                if (onFavorite != null) {
                    FavoriteButton(isFavorited = isFavorited, onClick = onFavorite)
                }
                IconButton(onClick = onShare) {
                    Icon(Icons.Outlined.Share, contentDescription = "Share", tint = Color.White)
                }
                IconButton(onClick = onViewFullscreen) {
                    Icon(Icons.Outlined.Search, contentDescription = "Fullscreen", tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ApodBadge(date: String) {
    val formatted = formatApodDate(date)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.SM))
            .background(NasaColor.MediaOverlay)
            .padding(horizontal = Space.SM, vertical = Space.XS),
    ) {
        Text(
            text = "APOD • $formatted",
            style = MaterialTheme.typography.overline,
            color = Color.White.copy(alpha = 0.9f),
        )
    }
}

private fun formatApodDate(date: String): String {
    return try {
        val parts = date.split("-")
        if (parts.size != 3) return date
        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val month = months.getOrNull(parts[1].toInt() - 1) ?: return date
        "$month ${parts[2].trimStart('0')}"
    } catch (_: Exception) {
        date
    }
}
