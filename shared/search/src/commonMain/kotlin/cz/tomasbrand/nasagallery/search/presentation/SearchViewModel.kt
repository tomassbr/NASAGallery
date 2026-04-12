package cz.tomasbrand.nasagallery.search.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import cz.tomasbrand.nasagallery.gallery.domain.model.GalleryItem
import cz.tomasbrand.nasagallery.gallery.domain.model.GalleryMediaType
import cz.tomasbrand.nasagallery.network.NasaApiConstants
import cz.tomasbrand.nasagallery.network.dto.GalleryResponseDto
import cz.tomasbrand.nasagallery.search.data.remote.SearchService
import com.russhwolf.settings.Settings
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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private const val DEBOUNCE_MS = 300L
private const val MIN_QUERY_LENGTH = 2
private const val KEY_RECENT_QUERIES = "recent_search_queries"
private const val MAX_RECENT_QUERIES = 10

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val service: SearchService,
    private val settings: Settings,
) : BaseScopedViewModel<SearchState, SearchIntent, SearchEvent>() {

    private val queryFlow = MutableStateFlow("")

    private var query: String by mutableStateOf("")
    private var results: ImmutableList<GalleryItem> by mutableStateOf(persistentListOf())
    private var isSearching: Boolean by mutableStateOf(false)
    private var hasMore: Boolean by mutableStateOf(false)
    private var currentPage: Int = 1
    private var error: ErrorResult? by mutableStateOf(null)
    private var recentQueries: ImmutableList<String> by mutableStateOf(loadRecentQueries())

    init {
        queryFlow
            .debounce(DEBOUNCE_MS)
            .filter { it.length >= MIN_QUERY_LENGTH }
            .onEach { performSearch(it, page = 1) }
            .launchIn(viewModelScope)
    }

    @Composable
    override fun getState() = SearchState(
        query = query,
        results = results,
        isSearching = isSearching,
        hasMore = hasMore,
        error = error,
        recentQueries = recentQueries,
    )

    override fun onViewAppeared() {}

    override fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.UpdateQuery -> {
                query = intent.query
                queryFlow.value = intent.query
                if (intent.query.isEmpty()) {
                    results = persistentListOf()
                    hasMore = false
                }
            }
            SearchIntent.Search -> {
                if (query.isNotBlank()) {
                    saveRecentQuery(query)
                    viewModelScope.launch { performSearch(query, page = 1) }
                }
            }
            SearchIntent.LoadMore -> {
                if (hasMore && !isSearching) {
                    viewModelScope.launch { performSearch(query, page = currentPage) }
                }
            }
            SearchIntent.ClearSearch -> {
                query = ""
                queryFlow.value = ""
                results = persistentListOf()
                error = null
            }
            is SearchIntent.SelectSuggestion -> {
                query = intent.query
                queryFlow.value = intent.query
            }
            is SearchIntent.OpenItem -> viewModelScope.launch {
                _events.emit(SearchEvent.NavigateToDetail(intent.item))
            }
        }
    }

    private suspend fun performSearch(q: String, page: Int) {
        if (page == 1) {
            isSearching = true
            results = persistentListOf()
            currentPage = 1
        }
        service.search(q, page = page)
            .alsoOnSuccess { response ->
                val newItems = response.toGalleryItems()
                results = if (page == 1) newItems.toImmutableList()
                          else (results + newItems).toImmutableList()
                val totalHits = response.collection.metadata.totalHits
                currentPage = page + 1
                hasMore = page * NasaApiConstants.IMAGES_PAGE_SIZE < totalHits
                isSearching = false
            }
            .alsoOnError {
                error = it
                isSearching = false
            }
    }

    private fun loadRecentQueries(): ImmutableList<String> {
        val raw = settings.getStringOrNull(KEY_RECENT_QUERIES) ?: return persistentListOf()
        return raw.split("|").filter { it.isNotBlank() }.toImmutableList()
    }

    private fun saveRecentQuery(q: String) {
        val updated = (listOf(q) + recentQueries.filter { it != q }).take(MAX_RECENT_QUERIES)
        settings.putString(KEY_RECENT_QUERIES, updated.joinToString("|"))
        recentQueries = updated.toImmutableList()
    }
}

private fun GalleryResponseDto.toGalleryItems(): List<GalleryItem> =
    collection.items.mapNotNull { item ->
        val data = item.data.firstOrNull() ?: return@mapNotNull null
        val thumbnailUrl = item.links?.firstOrNull { it.rel == "preview" }?.href ?: return@mapNotNull null
        GalleryItem(
            nasaId = data.nasaId,
            title = data.title,
            description = data.description,
            mediaType = GalleryMediaType.from(data.mediaType),
            thumbnailUrl = thumbnailUrl,
            dateCreated = data.dateCreated,
            photographer = data.photographer,
            keywords = data.keywords ?: emptyList(),
            center = data.center,
        )
    }

data class SearchState(
    val query: String = "",
    val results: ImmutableList<GalleryItem> = persistentListOf(),
    val isSearching: Boolean = false,
    val hasMore: Boolean = false,
    val error: ErrorResult? = null,
    val recentQueries: ImmutableList<String> = persistentListOf(),
) : VmState

sealed interface SearchIntent : VmIntent {
    data class UpdateQuery(val query: String) : SearchIntent
    data object Search : SearchIntent
    data object LoadMore : SearchIntent
    data object ClearSearch : SearchIntent
    data class SelectSuggestion(val query: String) : SearchIntent
    data class OpenItem(val item: GalleryItem) : SearchIntent
}

sealed interface SearchEvent : VmEvent {
    data class NavigateToDetail(val item: GalleryItem) : SearchEvent
}
