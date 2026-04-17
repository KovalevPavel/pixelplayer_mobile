package kovp.pixelplayer.feature_login

import kovp.pixelplayer.core_ui.components.message_dialog.MessageDialogVs
import kotlin.jvm.JvmInline

sealed interface LoginEvent {
    data class ShowError(val viewState: MessageDialogVs) : LoginEvent

    @JvmInline
    value class ShowLoader(val show: Boolean) : LoginEvent

    data class NavigateToMainFlow(
        val token: String,
        val endpoint: String,
    ) : LoginEvent

    @JvmInline
    value class NavigateToStep(val step: Int) : LoginEvent
}
