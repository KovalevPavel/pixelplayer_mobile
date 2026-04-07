package kovp.pixelplayer.feature_settings.presentation

import kovp.pixelplayer.core.language.LanguageSelection

sealed interface SettingsAction {
    data object Logout : SettingsAction
    data object ChangeEndpoint : SettingsAction
    data object OnChangeLanguageClick : SettingsAction
    data class ChangeLanguage(val selection: LanguageSelection) : SettingsAction
    data class OnMessageDialogPrimaryClick(val dialogId: String) : SettingsAction
}
