package kov_p.pixelplayer.feature_settings.presentation

import kov_p.pixelplayer.core_ui.components.message_dialog.MessageDialogVs

sealed interface SettingsEvent {
    data class ShowMessageDialog(
        val viewState: MessageDialogVs,
    ) : SettingsEvent

    data object ShowLanguagesDialog : SettingsEvent

    data object NavigateToLoginFlow : SettingsEvent
}
