package kovp.pixelplayer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kovp.pixelplayer.api_credentials.CredentialsRepository
import kovp.pixelplayer.core_ui.launch

class MainViewModel(
    private val credentialsRepository: CredentialsRepository,
) : ViewModel() {
    private val _eventsFlow = MutableSharedFlow<MainEvent>()
    val event: Flow<MainEvent> = _eventsFlow

    init {
        MainAction.CheckCredentials.let(::handleAction)
    }

    fun handleAction(action: MainAction) {
        when (action) {
            is MainAction.CheckCredentials -> {
                checkCredentials()
            }
        }
    }

    private fun checkCredentials() {
        launch(
            body = {
                delay(MIN_DELAY_MS)
                val endpoint = credentialsRepository.getEndpoint()
                val token = credentialsRepository.getToken()

                val event = when {
                    token?.isNotEmpty() == true -> {
                        MainEvent.CheckResult.OpenMain(
                            token = token,
                            endpoint = endpoint.orEmpty(),
                        )
                    }

                    endpoint?.isNotEmpty() == true -> MainEvent.CheckResult.EmptyCreds
                    else -> MainEvent.CheckResult.EmptyEndpoint
                }
                    .let(MainEvent::LaunchMainHost)

                _eventsFlow.emit(event)
            },
        )
    }

    companion object {
        private const val MIN_DELAY_MS = 500L
    }
}
