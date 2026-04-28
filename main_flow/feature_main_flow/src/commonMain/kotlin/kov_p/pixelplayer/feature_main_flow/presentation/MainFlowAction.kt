package kov_p.pixelplayer.feature_main_flow.presentation

sealed interface MainFlowAction {
    data object CheckDemoAppNotice : MainFlowAction
}
