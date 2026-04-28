package kov_p.pixelplayer.feature_main_flow.presentation

import kov_p.pixelplayer.core_ui.components.message_dialog.MessageDialogVs

sealed interface MainFlowEvent {
    data class ShowMessageDialog(val viewState: MessageDialogVs) : MainFlowEvent
}
