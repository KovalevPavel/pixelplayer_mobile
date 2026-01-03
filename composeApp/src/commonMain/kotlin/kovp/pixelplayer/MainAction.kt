package kovp.pixelplayer

sealed interface MainAction {
    data object CheckCredentials : MainAction
}
