package kovp.pixelplayer.core_ui.components.player

sealed interface PlayerAction {
    data object Resume : PlayerAction
    data object Pause : PlayerAction
    data object Next : PlayerAction
    data object Previous : PlayerAction
    data class Seek(val fraction: Float) : PlayerAction
}
