package cz.tomasbrand.nasagallery.gallery.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import cz.tomasbrand.nasagallery.gallery.data.repository.GalleryRepository
import cz.tomasbrand.nasagallery.gallery.domain.model.GalleryItem
import kmp.shared.base.domain.model.ErrorResult
import kmp.shared.base.domain.util.extension.alsoOnError
import kmp.shared.base.domain.util.extension.alsoOnSuccess
import kmp.shared.base.presentation.vm.BaseScopedViewModel
import kmp.shared.base.presentation.vm.VmEvent
import kmp.shared.base.presentation.vm.VmIntent
import kmp.shared.base.presentation.vm.VmState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

class GalleryViewModel(
    private val repository: GalleryRepository,
) : BaseScopedViewModel<GalleryState, GalleryIntent, GalleryEvent>() {

    private var items: ImmutableList<GalleryItem> by mutableStateOf(persistentListOf())
    private var isLoading: Boolean by mutableStateOf(false)
    private var isLoadingMore: Boolean by mutableStateOf(false)
    private var hasMore: Boolean by mutableStateOf(true)
    private var error: ErrorResult? by mutableStateOf(null)
    private var currentPage: Int = 1

    @Composable
    override fun getState() = GalleryState(
        items = items,
        isLoading = isLoading,
        isLoadingMore = isLoadingMore,
        hasMore = hasMore,
        error = error,
    )

    override fun onViewAppeared() {
        loadInitial()
    }

    override fun onIntent(intent: GalleryIntent) {
        when (intent) {
            GalleryIntent.LoadInitial -> loadInitial()
            GalleryIntent.LoadMore -> if (hasMore && !isLoadingMore) loadMore()
            is GalleryIntent.OpenItem -> viewModelScope.launch {
                _events.emit(GalleryEvent.NavigateToDetail(intent.item))
            }
            GalleryIntent.DismissError -> error = null
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            isLoading = true
            error = null
            currentPage = 1
            repository.getPage(currentPage)
                .alsoOnSuccess { page ->
                    items = page.items.toImmutableList()
                    hasMore = page.hasMore
                    currentPage = page.nextPage
                    isLoading = false
                }
                .alsoOnError {
                    error = it
                    isLoading = false
                }
        }
    }

    private fun loadMore() {
        viewModelScope.launch {
            isLoadingMore = true
            repository.getPage(currentPage)
                .alsoOnSuccess { page ->
                    items = (items + page.items).toImmutableList()
                    hasMore = page.hasMore
                    currentPage = page.nextPage
                    isLoadingMore = false
                }
                .alsoOnError {
                    isLoadingMore = false
                }
        }
    }
}

data class GalleryState(
    val items: ImmutableList<GalleryItem> = persistentListOf(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val error: ErrorResult? = null,
) : VmState

sealed interface GalleryIntent : VmIntent {
    data object LoadInitial : GalleryIntent
    data object LoadMore : GalleryIntent
    data class OpenItem(val item: GalleryItem) : GalleryIntent
    data object DismissError : GalleryIntent
}

sealed interface GalleryEvent : VmEvent {
    data class NavigateToDetail(val item: GalleryItem) : GalleryEvent
}
