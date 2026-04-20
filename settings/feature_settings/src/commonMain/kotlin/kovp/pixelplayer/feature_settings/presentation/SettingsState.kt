package kovp.pixelplayer.feature_settings.presentation

import kovp.pixelplayer.core.language.AppLanguage
import kovp.pixelplayer.core.language.LanguageSelection

sealed interface SettingsState {
    data object Loading : SettingsState

    data class Data(
        val login: String,
        val endpoint: String,
        val languageSelection: LanguageSelection,
        val deviceLanguage: AppLanguage,
        val isLanguagePickerVisible: Boolean,
        val isDemo: Boolean,
        val isProcessing: Boolean,
    ) : SettingsState
}
