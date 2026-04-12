package cz.tomasbrand.nasagallery.apod.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import cz.tomasbrand.nasagallery.apod.domain.model.Apod
import cz.tomasbrand.nasagallery.apod.domain.usecase.GetApodForDateUseCase
import cz.tomasbrand.nasagallery.apod.domain.usecase.GetTodayApodUseCase
import kmp.shared.base.domain.model.ErrorResult
import kmp.shared.base.domain.util.extension.alsoOnError
import kmp.shared.base.domain.util.extension.alsoOnSuccess
import kmp.shared.base.presentation.vm.BaseScopedViewModel
import kmp.shared.base.presentation.vm.VmEvent
import kmp.shared.base.presentation.vm.VmIntent
import kmp.shared.base.presentation.vm.VmState
import kotlinx.coroutines.launch

class ApodViewModel(
    private val getTodayApod: GetTodayApodUseCase,
    private val getApodForDate: GetApodForDateUseCase,
) : BaseScopedViewModel<ApodState, ApodIntent, ApodEvent>() {

    private var apod: Apod? by mutableStateOf(null)
    private var isLoading: Boolean by mutableStateOf(false)
    private var error: ErrorResult? by mutableStateOf(null)
    private var selectedDate: String? by mutableStateOf(null)

    @Composable
    override fun getState() = ApodState(
        apod = apod,
        isLoading = isLoading,
        error = error,
        selectedDate = selectedDate,
    )

    override fun onViewAppeared() {
        loadToday()
    }

    override fun onIntent(intent: ApodIntent) {
        when (intent) {
            is ApodIntent.LoadToday -> loadToday()
            is ApodIntent.LoadForDate -> loadForDate(intent.date)
            is ApodIntent.OpenFullscreen -> viewModelScope.launch {
                _events.emit(ApodEvent.OpenFullscreen(intent.apod))
            }
            is ApodIntent.Share -> viewModelScope.launch {
                apod?.let { _events.emit(ApodEvent.Share(it)) }
            }
            is ApodIntent.DismissError -> error = null
        }
    }

    private fun loadToday() {
        viewModelScope.launch {
            isLoading = true
            error = null
            getTodayApod()
                .alsoOnSuccess {
                    apod = it
                    isLoading = false
                }
                .alsoOnError {
                    error = it
                    isLoading = false
                }
        }
    }

    private fun loadForDate(date: String) {
        viewModelScope.launch {
            isLoading = true
            error = null
            selectedDate = date
            getApodForDate(date)
                .alsoOnSuccess {
                    apod = it
                    isLoading = false
                }
                .alsoOnError {
                    error = it
                    isLoading = false
                }
        }
    }
}

data class ApodState(
    val apod: Apod? = null,
    val isLoading: Boolean = false,
    val error: ErrorResult? = null,
    val selectedDate: String? = null,
) : VmState

sealed interface ApodIntent : VmIntent {
    data object LoadToday : ApodIntent
    data class LoadForDate(val date: String) : ApodIntent
    data class OpenFullscreen(val apod: Apod) : ApodIntent
    data object Share : ApodIntent
    data object DismissError : ApodIntent
}

sealed interface ApodEvent : VmEvent {
    data class OpenFullscreen(val apod: Apod) : ApodEvent
    data class Share(val apod: Apod) : ApodEvent
}
