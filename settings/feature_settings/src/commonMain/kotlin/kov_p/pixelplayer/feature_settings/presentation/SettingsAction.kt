package kov_p.pixelplayer.feature_settings.presentation

import kov_p.pixelplayer.core.language.LanguageSelection

sealed interface SettingsAction {
    data object Logout : SettingsAction
    data object ChangeEndpoint : SettingsAction
    data object OnChangeLanguageClick : SettingsAction
    data class ChangeLanguage(val selection: LanguageSelection) : SettingsAction
    data class OnMessageDialogPrimaryClick(val dialogId: String) : SettingsAction
}
