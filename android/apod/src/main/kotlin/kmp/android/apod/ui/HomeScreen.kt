package kmp.android.apod.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cz.tomasbrand.nasagallery.apod.presentation.ApodEvent
import cz.tomasbrand.nasagallery.apod.presentation.ApodIntent
import cz.tomasbrand.nasagallery.apod.presentation.ApodState
import cz.tomasbrand.nasagallery.apod.presentation.ApodViewModel
import cz.tomasbrand.nasagallery.gallery.presentation.GalleryIntent
import cz.tomasbrand.nasagallery.gallery.presentation.GalleryState
import cz.tomasbrand.nasagallery.gallery.presentation.GalleryViewModel
import kmp.android.shared.components.ApodHeroCard
import kmp.android.shared.components.NasaAsyncImage
import kmp.android.shared.components.NasaChip
import kmp.android.shared.components.NasaErrorView
import kmp.android.shared.components.NasaLoadingView
import kmp.android.shared.style.NasaColor
import kmp.android.shared.style.Radius
import kmp.android.shared.style.Space
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun HomeRoute(navController: NavHostController) {
    val apodViewModel: ApodViewModel = koinViewModel()
    val galleryViewModel: GalleryViewModel = koinViewModel()
    val apodState by apodViewModel.state.collectAsStateWithLifecycle()
    val galleryState by galleryViewModel.state.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(apodViewModel) {
        apodViewModel.onViewAppeared()
        apodViewModel.events.collectLatest { event ->
            if (event is ApodEvent.Share) {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, event.apod.title)
                    putExtra(Intent.EXTRA_TEXT, "${event.apod.title}\n\n${event.apod.url}")
                }
                context.startActivity(Intent.createChooser(intent, "Share"))
            }
        }
    }

    LaunchedEffect(galleryViewModel) { galleryViewModel.onViewAppeared() }

    HomeScreen(
        apodState = apodState,
        galleryState = galleryState,
        onApodIntent = apodViewModel::onIntent,
        onGalleryIntent = galleryViewModel::onIntent,
    )
}

private val filters = listOf("All Sources", "APOD", "Mars Rovers", "EPIC")

@Composable
private fun HomeScreen(
    apodState: ApodState,
    galleryState: GalleryState,
    onApodIntent: (ApodIntent) -> Unit,
    onGalleryIntent: (GalleryIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NasaColor.Background),
    ) {
        HomeTopBar()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            FilterSection()
            Spacer(Modifier.height(Space.LG))
            TodaysPickSection(apodState = apodState, onApodIntent = onApodIntent)
            Spacer(Modifier.height(Space.LG))
            ExploreSection(galleryState = galleryState, onGalleryIntent = onGalleryIntent)
        }
    }
}

// MARK: - Top Bar

@Composable
private fun HomeTopBar() {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "NASA Gallery",
                    style = MaterialTheme.typography.h6,
                    color = NasaColor.OnBackground,
                )
                Text(
                    text = "EXPLORATION DATA",
                    style = MaterialTheme.typography.overline,
                    color = NasaColor.Subtle,
                )
            }
        },
        backgroundColor = NasaColor.Background,
        elevation = 0.dp,
    )
}

// MARK: - Filter Section

@Composable
private fun FilterSection() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Space.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(Space.SM),
    ) {
        items(filters) { filter ->
            NasaChip(title = filter, isSelected = filter == "All Sources")
        }
    }
}

// MARK: - Today's Pick

@Composable
private fun TodaysPickSection(
    apodState: ApodState,
    onApodIntent: (ApodIntent) -> Unit,
) {
    Text(
        text = "Today's Pick",
        style = MaterialTheme.typography.subtitle1,
        color = NasaColor.OnBackground,
        modifier = Modifier.padding(horizontal = Space.screenHorizontal),
    )
    Spacer(Modifier.height(Space.SM))
    ApodContent(state = apodState, onIntent = onApodIntent)
}

@Composable
private fun ApodContent(
    state: ApodState,
    onIntent: (ApodIntent) -> Unit,
) {
    when {
        state.isLoading -> NasaLoadingView(Modifier.fillMaxWidth().height(280.dp))
        state.apod != null -> ApodHeroCard(
            title = state.apod!!.title,
            imageUrl = state.apod!!.displayUrl,
            date = state.apod!!.date,
            copyright = state.apod!!.copyright,
            onShare = { onIntent(ApodIntent.Share) },
            onViewFullscreen = { onIntent(ApodIntent.OpenFullscreen(state.apod!!)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Space.screenHorizontal),
        )
        state.error != null -> NasaErrorView(
            message = state.error?.throwable?.message ?: "Error",
            modifier = Modifier.height(200.dp).padding(horizontal = Space.screenHorizontal),
            onRetry = { onIntent(ApodIntent.LoadToday) },
        )
    }
}

// MARK: - Explore Section

@Composable
private fun ExploreSection(
    galleryState: GalleryState,
    onGalleryIntent: (GalleryIntent) -> Unit,
) {
    if (galleryState.items.isEmpty()) return
    ExploreSectionHeader()
    Spacer(Modifier.height(Space.SM))
    GalleryCarousel(items = galleryState.items.take(10))
    Spacer(Modifier.height(Space.XXXL))
}

@Composable
private fun ExploreSectionHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Space.screenHorizontal),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Explore", style = MaterialTheme.typography.subtitle1, color = NasaColor.OnBackground)
            Text(text = "NASA IMAGERY", style = MaterialTheme.typography.overline, color = NasaColor.Subtle)
        }
        TextButton(onClick = {}) {
            Text(text = "See all", style = MaterialTheme.typography.caption, color = NasaColor.Accent)
        }
    }
}

@Composable
private fun GalleryCarousel(items: List<cz.tomasbrand.nasagallery.gallery.domain.model.GalleryItem>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Space.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(Space.SM),
    ) {
        items(items) { item ->
            GalleryCarouselCard(title = item.title, thumbnailUrl = item.thumbnailUrl, onClick = {})
        }
    }
}

// MARK: - Gallery Carousel Card

@Composable
private fun GalleryCarouselCard(
    title: String,
    thumbnailUrl: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(Radius.MD))
            .clickable(onClick = onClick),
    ) {
        CardImage(thumbnailUrl = thumbnailUrl, contentDescription = title)
        CardGradientOverlay()
        CardTitle(
            title = title,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(Space.SM),
        )
    }
}

@Composable
private fun CardImage(thumbnailUrl: String, contentDescription: String) {
    NasaAsyncImage(
        url = thumbnailUrl,
        contentDescription = contentDescription,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun CardGradientOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                ),
            ),
    )
}

@Composable
private fun CardTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.caption,
        color = Color.White,
        maxLines = 2,
        modifier = modifier,
    )
}
