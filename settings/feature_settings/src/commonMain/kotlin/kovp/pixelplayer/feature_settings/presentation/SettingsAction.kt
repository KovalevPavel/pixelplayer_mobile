package kovp.pixelplayer.feature_settings.presentation

sealed interface SettingsAction {
    data object Logout : SettingsAction
    data object ChangeEndpoint : SettingsAction
    data class OnMessageDialogPrimaryClick(val dialogId: String) : SettingsAction
}
