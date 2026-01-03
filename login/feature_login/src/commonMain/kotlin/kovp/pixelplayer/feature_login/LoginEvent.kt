package kovp.pixelplayer.feature_login

import kovp.pixelplayer.core_ui.components.MessageDialogVs

sealed interface LoginEvent {
    data class ShowError(val viewState: MessageDialogVs) : LoginEvent
    data class NavigateNext(
        val token: String? = null,
        val endpoint: String,
    ) : LoginEvent

    data object NavigatePrevious : LoginEvent
}
