package kovp.pixelplayer.initializer

sealed interface InitializerEvent {
    data object OpenLoginFlow : InitializerEvent

    data class OpenMainFlow(
        val token: String,
        val endpoint: String,
    ) : InitializerEvent
}
