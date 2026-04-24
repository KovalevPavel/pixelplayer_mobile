package kovp.pixelplayer.main

sealed interface MainAction {
    data object Initialize : MainAction
}
