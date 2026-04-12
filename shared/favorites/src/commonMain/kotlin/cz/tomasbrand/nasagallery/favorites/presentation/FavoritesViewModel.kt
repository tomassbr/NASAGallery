package cz.tomasbrand.nasagallery.favorites.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import cz.tomasbrand.nasagallery.favorites.data.local.FavoritesSource
import cz.tomasbrand.nasagallery.favorites.domain.model.Favorite
import kmp.shared.base.presentation.vm.BaseScopedViewModel
import kmp.shared.base.presentation.vm.VmEvent
import kmp.shared.base.presentation.vm.VmIntent
import kmp.shared.base.presentation.vm.VmState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val source: FavoritesSource,
) : BaseScopedViewModel<FavoritesState, FavoritesIntent, FavoritesEvent>() {

    private var favorites: ImmutableList<Favorite> by mutableStateOf(persistentListOf())
    private var pendingRemoval: Favorite? by mutableStateOf(null)

    init {
        source.observeAll()
            .onEach { favorites = it.toImmutableList() }
            .launchIn(viewModelScope)
    }

    @Composable
    override fun getState() = FavoritesState(
        favorites = favorites,
        isEmpty = favorites.isEmpty(),
    )

    override fun onViewAppeared() {}

    override fun onIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.Remove -> {
                pendingRemoval = favorites.firstOrNull { it.nasaId == intent.nasaId }
                source.remove(intent.nasaId)
                viewModelScope.launch {
                    _events.emit(FavoritesEvent.ShowUndoSnackbar(intent.nasaId))
                }
            }
            is FavoritesIntent.UndoRemove -> {
                pendingRemoval?.let { source.add(it) }
                pendingRemoval = null
            }
            is FavoritesIntent.OpenDetail -> viewModelScope.launch {
                _events.emit(FavoritesEvent.NavigateToDetail(intent.nasaId))
            }
        }
    }
}

data class FavoritesState(
    val favorites: ImmutableList<Favorite> = persistentListOf(),
    val isEmpty: Boolean = true,
) : VmState

sealed interface FavoritesIntent : VmIntent {
    data class Remove(val nasaId: String) : FavoritesIntent
    data class UndoRemove(val nasaId: String) : FavoritesIntent
    data class OpenDetail(val nasaId: String) : FavoritesIntent
}

sealed interface FavoritesEvent : VmEvent {
    data class ShowUndoSnackbar(val nasaId: String) : FavoritesEvent
    data class NavigateToDetail(val nasaId: String) : FavoritesEvent
}
