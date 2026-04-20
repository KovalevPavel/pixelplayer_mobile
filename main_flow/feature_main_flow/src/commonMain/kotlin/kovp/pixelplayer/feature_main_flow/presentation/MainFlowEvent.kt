package kovp.pixelplayer.feature_main_flow.presentation

import kovp.pixelplayer.core_ui.components.message_dialog.MessageDialogVs

sealed interface MainFlowEvent {
    data class ShowMessageDialog(val viewState: MessageDialogVs) : MainFlowEvent
}
