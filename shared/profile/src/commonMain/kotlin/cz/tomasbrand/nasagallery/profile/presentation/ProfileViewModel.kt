package cz.tomasbrand.nasagallery.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import cz.tomasbrand.nasagallery.profile.domain.ImageCacheClearer
import kmp.shared.base.data.preferences.NasaApiKeyStore
import kmp.shared.base.presentation.vm.BaseScopedViewModel
import kmp.shared.base.presentation.vm.VmEvent
import kmp.shared.base.presentation.vm.VmIntent
import kmp.shared.base.presentation.vm.VmState
import kotlinx.coroutines.launch

private const val KEY_DARK_THEME = "dark_theme_enabled"
private const val KEY_DATA_SAVER = "data_saver_enabled"
private const val DEFAULT_API_KEY = "DEMO_KEY"

class ProfileViewModel(
    private val settings: Settings,
    private val nasaApiKeyStore: NasaApiKeyStore,
    private val imageCacheClearer: ImageCacheClearer,
) : BaseScopedViewModel<ProfileState, ProfileIntent, ProfileEvent>() {

    private var apiKey: String by mutableStateOf(nasaApiKeyStore.getOverrideOrNull() ?: DEFAULT_API_KEY)
    private var darkThemeEnabled: Boolean by mutableStateOf(settings.getBoolean(KEY_DARK_THEME, true))
    private var dataSaverEnabled: Boolean by mutableStateOf(settings.getBoolean(KEY_DATA_SAVER, false))
    private var isEditingApiKey: Boolean by mutableStateOf(false)

    @Composable
    override fun getState() = ProfileState(
        apiKey = apiKey,
        darkThemeEnabled = darkThemeEnabled,
        dataSaverEnabled = dataSaverEnabled,
        isEditingApiKey = isEditingApiKey,
    )

    override fun onViewAppeared() {}

    override fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.EditApiKey -> isEditingApiKey = true
            is ProfileIntent.SaveApiKey -> {
                val trimmed = intent.key.trim()
                if (trimmed.isNotEmpty()) {
                    apiKey = trimmed
                    nasaApiKeyStore.putOverride(trimmed)
                }
                isEditingApiKey = false
            }
            is ProfileIntent.CancelApiKeyEdit -> isEditingApiKey = false
            is ProfileIntent.ToggleDarkTheme -> {
                darkThemeEnabled = intent.enabled
                settings.putBoolean(KEY_DARK_THEME, intent.enabled)
            }
            is ProfileIntent.ToggleDataSaver -> {
                dataSaverEnabled = intent.enabled
                settings.putBoolean(KEY_DATA_SAVER, intent.enabled)
            }
            is ProfileIntent.ClearCache -> viewModelScope.launch {
                imageCacheClearer.clear()
                _events.emit(ProfileEvent.CacheCleared)
            }
        }
    }
}

data class ProfileState(
    val apiKey: String = DEFAULT_API_KEY,
    val darkThemeEnabled: Boolean = true,
    val dataSaverEnabled: Boolean = false,
    val isEditingApiKey: Boolean = false,
) : VmState

sealed interface ProfileIntent : VmIntent {
    data object EditApiKey : ProfileIntent
    data class SaveApiKey(val key: String) : ProfileIntent
    data object CancelApiKeyEdit : ProfileIntent
    data class ToggleDarkTheme(val enabled: Boolean) : ProfileIntent
    data class ToggleDataSaver(val enabled: Boolean) : ProfileIntent
    data object ClearCache : ProfileIntent
}

sealed interface ProfileEvent : VmEvent {
    data object CacheCleared : ProfileEvent
}
