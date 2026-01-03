package kovp.pixelplayer

sealed interface MainEvent {
    data class LaunchMainHost(val result: CheckResult) : MainEvent

    sealed interface CheckResult {
        data object EmptyEndpoint : CheckResult
        data object EmptyCreds : CheckResult
        data class OpenMain(
            val token: String,
            val endpoint: String,
        ) : CheckResult
    }
}
