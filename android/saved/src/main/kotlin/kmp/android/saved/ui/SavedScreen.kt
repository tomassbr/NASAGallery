package kmp.android.saved.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cz.tomasbrand.nasagallery.favorites.domain.model.Favorite
import cz.tomasbrand.nasagallery.favorites.presentation.FavoritesEvent
import cz.tomasbrand.nasagallery.favorites.presentation.FavoritesIntent
import cz.tomasbrand.nasagallery.favorites.presentation.FavoritesState
import cz.tomasbrand.nasagallery.favorites.presentation.FavoritesViewModel
import kmp.android.shared.components.FavoriteButton
import kmp.android.shared.components.NasaAsyncImage
import kmp.android.shared.components.NasaEmptyView
import kmp.android.shared.style.NasaColor
import kmp.android.shared.style.Radius
import kmp.android.shared.style.Space
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun SavedRoute(navController: NavHostController) {
    val viewModel: FavoritesViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.onViewAppeared()
        viewModel.events.collectLatest { event ->
            if (event is FavoritesEvent.ShowUndoSnackbar) {
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Removed from favorites",
                        actionLabel = "Undo",
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onIntent(FavoritesIntent.UndoRemove(event.nasaId))
                    }
                }
            }
        }
    }

    FavoritesScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun FavoritesScreen(
    state: FavoritesState,
    snackbarHostState: SnackbarHostState,
    onIntent: (FavoritesIntent) -> Unit,
) {
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
            FavoritesTopBar(count = state.favorites.size, isEmpty = state.isEmpty)
            FavoritesContent(state = state, onIntent = onIntent)
        }
    }
}

// MARK: - Top Bar

@Composable
private fun FavoritesTopBar(count: Int, isEmpty: Boolean) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Favorites",
                    style = MaterialTheme.typography.h6,
                    color = NasaColor.OnBackground,
                )
                if (!isEmpty) {
                    Text(
                        text = "$count SAVED",
                        style = MaterialTheme.typography.overline,
                        color = NasaColor.Subtle,
                    )
                }
            }
        },
        backgroundColor = NasaColor.Background,
        elevation = 0.dp,
    )
}

// MARK: - Content

@Composable
private fun FavoritesContent(
    state: FavoritesState,
    onIntent: (FavoritesIntent) -> Unit,
) {
    if (state.isEmpty) {
        FavoritesEmptyState()
    } else {
        FavoritesList(items = state.favorites, onIntent = onIntent)
    }
}

@Composable
private fun FavoritesEmptyState() {
    NasaEmptyView(
        title = "No favorites yet",
        message = "Tap the heart on any image to save it here",
        modifier = Modifier.fillMaxSize().padding(Space.screenHorizontal),
    )
}

@Composable
private fun FavoritesList(
    items: List<Favorite>,
    onIntent: (FavoritesIntent) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items, key = { it.nasaId }) { item ->
            FavoriteListRow(
                item = item,
                onRemove = { onIntent(FavoritesIntent.Remove(item.nasaId)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Space.screenHorizontal, vertical = Space.XS),
            )
        }
    }
}

// MARK: - List Row

@Composable
private fun FavoriteListRow(
    item: Favorite,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.MD))
            .background(NasaColor.Surface)
            .padding(Space.MD),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RowThumbnail(thumbnailUrl = item.thumbnailUrl, contentDescription = item.title)
        Spacer(Modifier.width(Space.MD))
        RowInfo(title = item.title, date = item.dateCreated, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(Space.SM))
        FavoriteButton(isFavorited = true, onClick = onRemove)
    }
}

@Composable
private fun RowThumbnail(thumbnailUrl: String, contentDescription: String) {
    NasaAsyncImage(
        url = thumbnailUrl,
        contentDescription = contentDescription,
        modifier = Modifier
            .size(72.dp, 56.dp)
            .clip(RoundedCornerShape(Radius.SM)),
    )
}

@Composable
private fun RowInfo(title: String, date: String?, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = title, style = MaterialTheme.typography.body2, color = NasaColor.OnSurface, maxLines = 2)
        if (date != null) {
            Spacer(Modifier.height(Space.XS))
            Text(text = date, style = MaterialTheme.typography.overline, color = NasaColor.Subtle)
        }
    }
}
