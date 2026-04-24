package kovp.pixelplayer.main

sealed interface MainEvent {
    data object OpenLoginFlow : MainEvent

    data class OpenMainFlow(
        val token: String,
        val endpoint: String,
    ) : MainEvent

    data object SplashChecksPassed : MainEvent
}
