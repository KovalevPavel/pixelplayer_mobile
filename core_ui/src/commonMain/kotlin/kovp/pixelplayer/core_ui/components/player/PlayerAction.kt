package kovp.pixelplayer.core_ui.components.player

sealed interface PlayerAction {
    data object Play : PlayerAction
    data object Stop : PlayerAction
    data object Pause : PlayerAction
    data object Next : PlayerAction
    data object Previous : PlayerAction
    data class Seek(val fraction: Float) : PlayerAction
}
