package kmp.shared.base.data.preferences

import com.russhwolf.settings.Settings
import kmp.shared.base.preferences.AppSettingsKeys

/**
 * User-editable NASA API key override in platform-secure storage (Android Keystore-backed prefs, iOS Keychain).
 * Migrates a value previously stored under [AppSettingsKeys.NASA_API_KEY] in [legacySettings].
 */
class NasaApiKeyStore(
    private val secureSettings: Settings,
    private val legacySettings: Settings,
) {
    private var migrationChecked = false

    private fun ensureMigrated() {
        if (migrationChecked) return
        migrationChecked = true
        val legacy = legacySettings.getStringOrNull(AppSettingsKeys.NASA_API_KEY)?.trim()
        val secure = secureSettings.getStringOrNull(AppSettingsKeys.NASA_API_KEY)?.trim()
        if (!legacy.isNullOrEmpty() && secure.isNullOrEmpty()) {
            secureSettings.putString(AppSettingsKeys.NASA_API_KEY, legacy)
            legacySettings.remove(AppSettingsKeys.NASA_API_KEY)
        }
    }

    fun getOverrideOrNull(): String? {
        ensureMigrated()
        return secureSettings.getStringOrNull(AppSettingsKeys.NASA_API_KEY)?.trim()?.takeIf { it.isNotEmpty() }
    }

    fun putOverride(value: String) {
        ensureMigrated()
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return
        secureSettings.putString(AppSettingsKeys.NASA_API_KEY, trimmed)
        legacySettings.remove(AppSettingsKeys.NASA_API_KEY)
    }

    fun clearOverride() {
        secureSettings.remove(AppSettingsKeys.NASA_API_KEY)
        legacySettings.remove(AppSettingsKeys.NASA_API_KEY)
    }
}
