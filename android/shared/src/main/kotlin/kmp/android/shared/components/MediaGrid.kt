package kmp.android.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kmp.android.shared.style.Space

@Composable
fun <T> MediaGrid(
    items: List<T>,
    hasMore: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(Space.MD),
        horizontalArrangement = Arrangement.spacedBy(Space.SM),
        verticalArrangement = Arrangement.spacedBy(Space.SM),
    ) {
        itemsIndexed(items) { index, item ->
            itemContent(item)

            // Trigger load more when approaching end
            if (hasMore && index >= items.size - 4) {
                LaunchedEffect(index) { onLoadMore() }
            }
        }

        if (isLoadingMore) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Space.MD),
                    contentAlignment = Alignment.Center,
                ) {
                    NasaFooterLoadingView()
                }
            }
        }
    }
}
