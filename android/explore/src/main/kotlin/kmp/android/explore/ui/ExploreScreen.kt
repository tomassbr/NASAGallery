package kmp.android.explore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cz.tomasbrand.nasagallery.gallery.presentation.GalleryEvent
import cz.tomasbrand.nasagallery.gallery.presentation.GalleryIntent
import cz.tomasbrand.nasagallery.gallery.presentation.GalleryState
import cz.tomasbrand.nasagallery.gallery.presentation.GalleryViewModel
import cz.tomasbrand.nasagallery.search.presentation.SearchEvent
import cz.tomasbrand.nasagallery.search.presentation.SearchIntent
import cz.tomasbrand.nasagallery.search.presentation.SearchState
import cz.tomasbrand.nasagallery.search.presentation.SearchViewModel
import kmp.android.explore.navigation.ExploreGraph
import kmp.android.shared.components.MediaCard
import kmp.android.shared.components.MediaGrid
import kmp.android.shared.components.NasaChip
import kmp.android.shared.components.NasaErrorView
import kmp.android.shared.components.NasaFullScreenLoadingView
import kmp.android.shared.components.NasaSearchBar
import kmp.android.shared.style.NasaColor
import kmp.android.shared.style.Space
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun ExploreRoute(navController: NavHostController) {
    val galleryViewModel: GalleryViewModel = koinViewModel()
    val searchViewModel: SearchViewModel = koinViewModel()
    val galleryState by galleryViewModel.state.collectAsStateWithLifecycle()
    val searchState by searchViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(galleryViewModel) {
        galleryViewModel.onViewAppeared()
        galleryViewModel.events.collectLatest { event ->
            when (event) {
                is GalleryEvent.NavigateToDetail -> {
                    navController.navigate(ExploreGraph.Detail(event.item.nasaId))
                }
            }
        }
    }

    LaunchedEffect(searchViewModel) {
        searchViewModel.events.collectLatest { event ->
            when (event) {
                is SearchEvent.NavigateToDetail -> {
                    navController.navigate(ExploreGraph.Detail(event.item.nasaId))
                }
            }
        }
    }

    ExploreScreen(
        galleryState = galleryState,
        searchState = searchState,
        onGalleryIntent = galleryViewModel::onIntent,
        onSearchIntent = searchViewModel::onIntent,
    )
}

private val mediaTypeFilters = listOf("All", "Images", "Videos")

@Composable
private fun ExploreScreen(
    galleryState: GalleryState,
    searchState: SearchState,
    onGalleryIntent: (GalleryIntent) -> Unit,
    onSearchIntent: (SearchIntent) -> Unit,
) {
    val isSearchActive = searchState.query.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NasaColor.Background),
    ) {
        TopAppBar(
            title = { Text("Explore", style = MaterialTheme.typography.h6, color = NasaColor.OnBackground) },
            backgroundColor = NasaColor.Background,
            elevation = 0.dp,
        )

        NasaSearchBar(
            value = searchState.query,
            onValueChange = { onSearchIntent(SearchIntent.UpdateQuery(it)) },
            onSearch = { onSearchIntent(SearchIntent.Search) },
            onClear = { onSearchIntent(SearchIntent.ClearSearch) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Space.screenHorizontal, vertical = Space.SM),
        )

        if (!isSearchActive) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = Space.screenHorizontal),
                modifier = Modifier.padding(bottom = Space.SM),
            ) {
                items(mediaTypeFilters) { filter ->
                    NasaChip(
                        title = filter,
                        isSelected = filter == "All",
                        onClick = {},
                    )
                    Spacer(Modifier.width(Space.SM))
                }
            }
        }

        when {
            isSearchActive -> {
                SearchResults(searchState = searchState, onSearchIntent = onSearchIntent)
            }
            galleryState.isLoading && galleryState.items.isEmpty() -> {
                NasaFullScreenLoadingView()
            }
            galleryState.error != null && galleryState.items.isEmpty() -> {
                NasaErrorView(
                    message = galleryState.error?.throwable?.message ?: "Something went wrong",
                    modifier = Modifier.fillMaxSize().padding(Space.screenHorizontal),
                    onRetry = { onGalleryIntent(GalleryIntent.LoadInitial) },
                )
            }
            else -> {
                MediaGrid(
                    items = galleryState.items,
                    hasMore = galleryState.hasMore,
                    isLoadingMore = galleryState.isLoadingMore,
                    onLoadMore = { onGalleryIntent(GalleryIntent.LoadMore) },
                ) { item ->
                    MediaCard(
                        nasaId = item.nasaId,
                        title = item.title,
                        thumbnailUrl = item.thumbnailUrl,
                        onTap = { onGalleryIntent(GalleryIntent.OpenItem(item)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResults(
    searchState: SearchState,
    onSearchIntent: (SearchIntent) -> Unit,
) {
    when {
        searchState.isSearching && searchState.results.isEmpty() -> NasaFullScreenLoadingView()
        searchState.error != null -> {
            NasaErrorView(
                message = searchState.error?.throwable?.message ?: "Search failed",
                modifier = Modifier.fillMaxSize().padding(Space.screenHorizontal),
                onRetry = { onSearchIntent(SearchIntent.Search) },
            )
        }
        else -> {
            MediaGrid(
                items = searchState.results,
                hasMore = searchState.hasMore,
                isLoadingMore = searchState.isSearching && searchState.results.isNotEmpty(),
                onLoadMore = { onSearchIntent(SearchIntent.LoadMore) },
            ) { item ->
                MediaCard(
                    nasaId = item.nasaId,
                    title = item.title,
                    thumbnailUrl = item.thumbnailUrl,
                    onTap = { onSearchIntent(SearchIntent.OpenItem(item)) },
                )
            }
        }
    }
}
