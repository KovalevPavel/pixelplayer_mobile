package kovp.pixelplayer.feature_login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kovp.pixelplayer.api_credentials.CredentialsRepository
import kovp.pixelplayer.core_ui.components.MessageDialogVs
import kovp.pixelplayer.core_ui.launch
import kovp.pixelplayer.domain_login.LoginRepository

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
                if (loginRepo.checkEndpoint(endpoint)) {
                    credentialsRepo.saveEndpoint(endpoint)
                    LoginEvent.NavigateNext(
                        token = null,
                        endpoint = endpoint,
                    )
                        .let(::emitEvent)
                    return@launch
                }

                error("Can't validate url. Try another one")
            },
            onFailure = {
                println(it)
                MessageDialogVs(
                    message = it.message,
                    primaryAction = "Ok",
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
                    credentialsRepo.saveToken(token)
                    LoginEvent.NavigateNext(
                        token = token,
                        endpoint = credentialsRepo.getEndpoint().orEmpty(),
                    )
                        .let(::emitEvent)
                    return@launch
                }

                error("Wrong credentials")
            },
            onFailure = {
                MessageDialogVs(
                    message = it.message,
                    primaryAction = "Ok",
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
}
