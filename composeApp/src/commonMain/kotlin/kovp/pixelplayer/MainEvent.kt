package kovp.pixelplayer

sealed interface MainEvent {
    data class LaunchMainHost(val result: CheckResult) : MainEvent

    enum class CheckResult {
        EmptyEndpoint,
        EmptyCreds,
        OpenMain,
    }
}
