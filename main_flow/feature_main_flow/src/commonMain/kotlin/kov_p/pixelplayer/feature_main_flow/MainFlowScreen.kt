package kov_p.pixelplayer.feature_main_flow

import kotlinx.serialization.Serializable

enum class MainFlowScreen {
    @Serializable
    Artists,

    @Serializable
    Albums,

    @Serializable
    Tracks,

    @Serializable
    Settings,
    ;

    val route: String get() = name

    companion object {
        @Serializable
        object Host
    }
}
