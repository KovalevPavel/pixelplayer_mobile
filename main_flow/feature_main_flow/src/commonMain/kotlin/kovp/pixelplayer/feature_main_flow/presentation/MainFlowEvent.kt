package kovp.pixelplayer.feature_main_flow.presentation

sealed interface MainFlowEvent {
    data object ShowTestAccountNotice : MainFlowEvent
}
