package kov_p.pixelplayer.feature_settings.presentation

import kov_p.pixelplayer.core.language.AppLanguage
import kov_p.pixelplayer.core.language.LanguageSelection

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
