package kovp.pixelplayer.feature_login

sealed interface LoginAction {
    data class CheckEndpoint(val endpoint: String) : LoginAction
    data class Login(
        val login: String,
        val password: String,
    ) : LoginAction

    data object ChangeEndpoint : LoginAction
}
