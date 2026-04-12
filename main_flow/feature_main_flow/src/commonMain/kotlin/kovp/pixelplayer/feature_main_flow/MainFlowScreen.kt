package kovp.pixelplayer.feature_main_flow

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

    val route: String
        get() = name

    companion object {
        fun fromRoute(route: String): MainFlowScreen? {
            return entries.firstOrNull { it.route == route }
        }

        @Serializable
        object Host
    }
}
