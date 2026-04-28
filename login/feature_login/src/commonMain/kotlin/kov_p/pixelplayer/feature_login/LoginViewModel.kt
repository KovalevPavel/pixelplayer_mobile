package kov_p.pixelplayer.feature_login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.toUri
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kov_p.pixelplayer.api_credentials.CredentialsRepository
import kov_p.pixelplayer.core_ui.UiText
import kov_p.pixelplayer.core_ui.components.message_dialog.MessageDialogVs
import kov_p.pixelplayer.core_ui.launch
import kov_p.pixelplayer.domain_login.LoginRepository
import pixelplayer.core_ui.generated.resources.Res
import pixelplayer.core_ui.generated.resources.ok
import pixelplayer.feature_login.generated.resources.cant_validate_url
import pixelplayer.feature_login.generated.resources.wrong_credentials
import pixelplayer.feature_login.generated.resources.Res as loginRes

class LoginViewModel(
    private val loginRepo: LoginRepository,
    private val credentialsRepo: CredentialsRepository,
) : ViewModel() {
    val screenState: StateFlow<LoginState> by lazy { _screenState }
    val eventsFlow: Flow<LoginEvent> by lazy { _eventsFlow }

    private val _screenState = MutableStateFlow<LoginState>(LoginState.Init)
    private val _eventsFlow = MutableSharedFlow<LoginEvent>(extraBufferCapacity = 5)

    private var loginSteps: ImmutableList<LoginState.LoginStep> = persistentListOf()

    fun handleAction(action: LoginAction) {
        when (action) {
            LoginAction.CheckCreds -> checkCreds()
            is LoginAction.CheckEndpoint -> checkEndpoint(endpoint = action.endpoint)
            is LoginAction.Login -> login(login = action.login, password = action.password)
            is LoginAction.ChangeEndpoint -> changeDestination()
        }
    }

    private fun checkCreds() {
        launch(
            body = {
                val endpoint = credentialsRepo.getEndpoint()

                loginSteps = persistentListOf(
                    LoginState.Endpoint(initEndpoint = endpoint.orEmpty()),
                    LoginState.Credentials,
                )

                _screenState.update {
                    LoginState.Data(
                        pages = loginSteps,
                        initPage = when {
                            endpoint.isNullOrEmpty() -> 0
                            else -> 1
                        },
                    )
                }

                LoginEvent.ShowLoader(show = false).let(::emitEvent)
            },
        )
    }

    private fun checkEndpoint(endpoint: String) {
        launch(
            body = {
                LoginEvent.ShowLoader(show = true).let(::emitEvent)
                val normalizedEndpoint = endpoint.withSchema()

                if (loginRepo.checkEndpoint(normalizedEndpoint)) {
                    credentialsRepo.saveEndpoint(normalizedEndpoint)
                    navigateToState(LoginState.Credentials)
                } else {
                    error("Can't parse url")
                }
            },
            onFailure = {
                MessageDialogVs(
                    message = UiText.Resource(loginRes.string.cant_validate_url),
                    primaryAction = UiText.Resource(Res.string.ok),
                )
                    .let(LoginEvent::ShowError)
                    .let(::emitEvent)
            },
            finally = {
                LoginEvent.ShowLoader(show = false).let(::emitEvent)
            },
        )
    }

    private fun login(login: String, password: String) {
        launch(
            body = {
                LoginEvent.ShowLoader(show = true).let(::emitEvent)
                val token = loginRepo.login(login = login, password = password)
                if (token.isNotEmpty()) {
                    credentialsRepo.saveUsername(username = login)
                    credentialsRepo.saveToken(token)
                    LoginEvent.NavigateToMainFlow(
                        token = token,
                        endpoint = credentialsRepo.getEndpoint().orEmpty(),
                    )
                        .let(::emitEvent)
                }
            },
            onFailure = {
                val message = when (it.message) {
                    WRONG_CREDS_ERROR -> UiText.Resource(loginRes.string.wrong_credentials)
                    else -> UiText.Dynamic(it.message)
                }

                MessageDialogVs(
                    message = message,
                    primaryAction = UiText.Resource(Res.string.ok),
                )
                    .let(LoginEvent::ShowError)
                    .let(::emitEvent)
            },
            finally = {
                LoginEvent.ShowLoader(show = false).let(::emitEvent)
            }
        )
    }

    private fun changeDestination() {
        launch(
            body = {
                credentialsRepo.saveEndpoint(null)
                navigateToState(LoginState.Endpoint(initEndpoint = ""))
            }
        )
    }

    private fun navigateToState(screen: LoginState.LoginStep) {
        loginSteps.indexOf(screen)
            .coerceAtLeast(0)
            .let(LoginEvent::NavigateToStep)
            .let(::emitEvent)
    }

    private fun emitEvent(event: LoginEvent) {
        viewModelScope.launch { _eventsFlow.emit(event) }
    }

    private fun String.withSchema(): String {
        return when {
            !this.toUri().scheme.isNullOrEmpty() -> this
            else -> "https://$this"
        }
    }

    companion object {
        private const val WRONG_CREDS_ERROR = "invalid input"
    }
}
