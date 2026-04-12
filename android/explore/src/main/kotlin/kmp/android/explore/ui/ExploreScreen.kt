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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.tomasbrand.nasagallery.favorites.presentation.FavoritesEvent
import cz.tomasbrand.nasagallery.favorites.presentation.FavoritesIntent
import cz.tomasbrand.nasagallery.favorites.presentation.FavoritesState
import cz.tomasbrand.nasagallery.favorites.presentation.FavoritesViewModel
import cz.tomasbrand.nasagallery.gallery.domain.model.GalleryItem
import cz.tomasbrand.nasagallery.gallery.presentation.GalleryEvent
import cz.tomasbrand.nasagallery.gallery.presentation.GalleryIntent
import cz.tomasbrand.nasagallery.gallery.presentation.GalleryState
import cz.tomasbrand.nasagallery.gallery.presentation.GalleryViewModel
import cz.tomasbrand.nasagallery.search.presentation.SearchEvent
import cz.tomasbrand.nasagallery.search.presentation.SearchIntent
import cz.tomasbrand.nasagallery.search.presentation.SearchState
import cz.tomasbrand.nasagallery.search.presentation.SearchViewModel
import kmp.android.shared.components.MediaCard
import kmp.android.shared.components.MediaGrid
import kmp.android.shared.components.NasaChip
import kmp.android.shared.components.NasaErrorView
import kmp.android.shared.components.NasaFullScreenLoadingView
import kmp.android.shared.components.NasaSearchBar
import kmp.android.shared.style.NasaColor
import kmp.android.shared.style.Space
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExploreRoute(
    onNavigateToDetail: (String) -> Unit,
) {
    val galleryViewModel: GalleryViewModel = koinViewModel()
    val searchViewModel: SearchViewModel = koinViewModel()
    val favoritesViewModel: FavoritesViewModel = koinViewModel()
    val galleryState by galleryViewModel.state.collectAsStateWithLifecycle()
    val searchState by searchViewModel.state.collectAsStateWithLifecycle()
    val favoritesState by favoritesViewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(galleryViewModel) {
        galleryViewModel.onViewAppeared()
        galleryViewModel.events.collectLatest { event ->
            when (event) {
                is GalleryEvent.NavigateToDetail -> onNavigateToDetail(event.item.nasaId)
            }
        }
    }

    LaunchedEffect(searchViewModel) {
        searchViewModel.events.collectLatest { event ->
            when (event) {
                is SearchEvent.NavigateToDetail -> onNavigateToDetail(event.item.nasaId)
            }
        }
    }

    LaunchedEffect(favoritesViewModel) {
        favoritesViewModel.events.collectLatest { event ->
            if (event is FavoritesEvent.SignInRequired) {
                scope.launch {
                    snackbarHostState.showSnackbar("Sign in to save favorites")
                }
            }
        }
    }

    ExploreScreen(
        galleryState = galleryState,
        searchState = searchState,
        favoritesState = favoritesState,
        snackbarHostState = snackbarHostState,
        onGalleryIntent = galleryViewModel::onIntent,
        onSearchIntent = searchViewModel::onIntent,
        onAddFavorite = { item -> favoritesViewModel.onIntent(FavoritesIntent.Add(item)) },
    )
}

private val mediaTypeFilters = listOf("All", "Images", "Videos")

@Composable
private fun ExploreScreen(
    galleryState: GalleryState,
    searchState: SearchState,
    favoritesState: FavoritesState,
    snackbarHostState: SnackbarHostState,
    onGalleryIntent: (GalleryIntent) -> Unit,
    onSearchIntent: (SearchIntent) -> Unit,
    onAddFavorite: (GalleryItem) -> Unit,
) {
    val isSearchActive = searchState.query.isNotEmpty()
    val favoriteIds = favoritesState.favorites.map { it.nasaId }.toSet()

    Scaffold(
        backgroundColor = NasaColor.Background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                    SearchResults(
                        searchState = searchState,
                        favoriteIds = favoriteIds,
                        onSearchIntent = onSearchIntent,
                        onAddFavorite = onAddFavorite,
                    )
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
                            isFavorited = item.nasaId in favoriteIds,
                            onTap = { onGalleryIntent(GalleryIntent.OpenItem(item)) },
                            onFavorite = { onAddFavorite(item) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResults(
    searchState: SearchState,
    favoriteIds: Set<String>,
    onSearchIntent: (SearchIntent) -> Unit,
    onAddFavorite: (GalleryItem) -> Unit,
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
                    isFavorited = item.nasaId in favoriteIds,
                    onTap = { onSearchIntent(SearchIntent.OpenItem(item)) },
                    onFavorite = { onAddFavorite(item) },
                )
            }
        }
    }
}
