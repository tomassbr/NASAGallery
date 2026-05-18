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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import cz.tomasbrand.nasagallery.gallery.domain.model.GalleryItem
import cz.tomasbrand.nasagallery.gallery.presentation.GalleryIntent
import cz.tomasbrand.nasagallery.gallery.presentation.GalleryState
import cz.tomasbrand.nasagallery.gallery.presentation.GalleryViewModel
import kmp.android.apod.navigation.ApodGraph
import kmp.android.shared.components.ApodHeroCard
import kmp.android.shared.components.NasaAsyncImage
import kmp.android.shared.components.NasaChip
import kmp.android.shared.components.NasaErrorView
import kmp.android.shared.components.NasaLoadingView
import kmp.android.shared.components.NasaPageHeader
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
        onOpenGalleryDetail = { nasaId -> navController.navigate(ApodGraph.MediaDetail(nasaId)) },
        onSeeAllExplore = { navController.navigate(ApodGraph.Explore()) },
    )
}

private val filters = listOf("All Sources", "APOD", "Mars Rovers", "EPIC")

@Composable
private fun HomeScreen(
    apodState: ApodState,
    galleryState: GalleryState,
    onApodIntent: (ApodIntent) -> Unit,
    onGalleryIntent: (GalleryIntent) -> Unit,
    onOpenGalleryDetail: (String) -> Unit,
    onSeeAllExplore: () -> Unit,
) {
    var selectedFilter by remember { mutableStateOf("All Sources") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NasaColor.Background),
    ) {
        NasaPageHeader(title = "NASA Gallery", subtitle = "EXPLORATION DATA")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            FilterSection(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
            )
            Spacer(Modifier.height(Space.LG))
            TodaysPickSection(apodState = apodState, onApodIntent = onApodIntent)
            Spacer(Modifier.height(Space.LG))
            ExploreSection(
                galleryState = galleryState,
                onGalleryIntent = onGalleryIntent,
                onOpenGalleryDetail = onOpenGalleryDetail,
                onSeeAllExplore = onSeeAllExplore,
            )
        }
    }
}

// MARK: - Filter Section

@Composable
private fun FilterSection(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Space.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(Space.SM),
    ) {
        items(filters) { filter ->
            NasaChip(
                title = filter,
                isSelected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) },
            )
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
            explanation = state.apod!!.explanation,
            copyright = state.apod!!.copyright,
            isFavorited = false,
            onFavorite = null,
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
    onOpenGalleryDetail: (String) -> Unit,
    onSeeAllExplore: () -> Unit,
) {
    Column {
        ExploreSectionHeader(onSeeAllExplore = onSeeAllExplore)
        Spacer(Modifier.height(Space.SM))
        when {
            galleryState.isLoading && galleryState.items.isEmpty() -> GalleryCarouselSkeleton()
            galleryState.error != null && galleryState.items.isEmpty() && !galleryState.isLoading -> {
                NasaErrorView(
                    message = galleryState.error?.throwable?.message ?: "Error",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(horizontal = Space.screenHorizontal),
                    onRetry = { onGalleryIntent(GalleryIntent.LoadInitial) },
                )
            }
            galleryState.items.isNotEmpty() -> {
                GalleryCarousel(
                    items = galleryState.items.take(10),
                    onItemClick = { onOpenGalleryDetail(it.nasaId) },
                )
            }
        }
        Spacer(Modifier.height(Space.XXXL))
    }
}

@Composable
private fun ExploreSectionHeader(onSeeAllExplore: () -> Unit) {
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
        TextButton(onClick = onSeeAllExplore) {
            Text(text = "See all", style = MaterialTheme.typography.caption, color = NasaColor.Accent)
        }
    }
}

@Composable
private fun GalleryCarouselSkeleton() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Space.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(Space.SM),
    ) {
        items(5) {
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(Radius.MD))
                    .background(NasaColor.Surface.copy(alpha = 0.5f)),
            )
        }
    }
}

@Composable
private fun GalleryCarousel(
    items: List<GalleryItem>,
    onItemClick: (GalleryItem) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Space.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(Space.SM),
    ) {
        items(items, key = { it.nasaId }) { item ->
            GalleryCarouselCard(
                title = item.title,
                thumbnailUrl = item.thumbnailUrl,
                onClick = { onItemClick(item) },
            )
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
        NasaAsyncImage(
            url = thumbnailUrl,
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                    ),
                ),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.caption,
            color = Color.White,
            maxLines = 2,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(Space.SM),
        )
    }
}
