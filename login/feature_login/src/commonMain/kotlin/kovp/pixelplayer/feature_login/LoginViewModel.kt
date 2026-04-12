package kovp.pixelplayer.feature_login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.toUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kovp.pixelplayer.api_credentials.CredentialsRepository
import kovp.pixelplayer.core_ui.UiText
import kovp.pixelplayer.core_ui.components.message_dialog.MessageDialogVs
import kovp.pixelplayer.core_ui.launch
import kovp.pixelplayer.domain_login.LoginRepository
import pixelplayer.core_ui.generated.resources.Res
import pixelplayer.core_ui.generated.resources.ok
import pixelplayer.feature_login.generated.resources.Res as loginRes
import pixelplayer.feature_login.generated.resources.cant_validate_url
import pixelplayer.feature_login.generated.resources.wrong_credentials

class LoginViewModel(
    private val loginRepo: LoginRepository,
    private val credentialsRepo: CredentialsRepository,
) : ViewModel() {
    val eventsFlow: Flow<LoginEvent> by lazy { _eventsFlow }
    private val _eventsFlow = MutableSharedFlow<LoginEvent>()

    fun handleAction(action: LoginAction) {
        when (action) {
            is LoginAction.CheckEndpoint -> checkEndpoint(endpoint = action.endpoint)
            is LoginAction.Login -> login(login = action.login, password = action.password)
            is LoginAction.ChangeEndpoint -> changeDestination()
        }
    }

    private fun checkEndpoint(endpoint: String) {
        launch(
            body = {
                val normalizedEndpoint = endpoint.withSchema()
                if (loginRepo.checkEndpoint(normalizedEndpoint)) {
                    credentialsRepo.saveEndpoint(normalizedEndpoint)
                    LoginEvent.NavigateNext(
                        token = null,
                        endpoint = normalizedEndpoint,
                    )
                        .let(::emitEvent)
                    return@launch
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
        )
    }

    private fun login(login: String, password: String) {
        launch(
            body = {
                val token = loginRepo.login(login = login, password = password)
                if (token.isNotEmpty()) {
                    credentialsRepo.saveUsername(username = login)
                    credentialsRepo.saveToken(token)
                    LoginEvent.NavigateNext(
                        token = token,
                        endpoint = credentialsRepo.getEndpoint().orEmpty(),
                    )
                        .let(::emitEvent)
                    return@launch
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
        )
    }

    private fun changeDestination() {
        launch(
            body = {
                credentialsRepo.saveEndpoint(null)
                LoginEvent.NavigatePrevious.let(::emitEvent)
            }
        )
    }

    private fun emitEvent(event: LoginEvent) {
        viewModelScope.launch { _eventsFlow.emit(event) }
    }

    private fun String.withSchema(): String {
        return if (!this.toUri().scheme.isNullOrEmpty()) {
            this
        } else {
            "$DEFAULT_SCHEMA://$this"
        }
    }

    companion object {
        private const val WRONG_CREDS_ERROR = "invalid input"
        private const val DEFAULT_SCHEMA = "https"
    }
}
