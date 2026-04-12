package kovp.pixelplayer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kovp.pixelplayer.api_credentials.CredentialsRepository
import kovp.pixelplayer.core_ui.launch

class MainViewModel(
    private val credentialsRepository: CredentialsRepository,
) : ViewModel() {
    val checkCredsStateFlow: StateFlow<MainEvent.CheckResult?> by lazy { _checkCredsStateFlow }
    private val _checkCredsStateFlow = MutableStateFlow<MainEvent.CheckResult?>(null)

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

                _checkCredsStateFlow.update {
                    when {
                        endpoint?.isNotEmpty() != true -> MainEvent.CheckResult.EmptyEndpoint
                        token?.isNotEmpty() == true -> {
                            MainEvent.CheckResult.OpenMain(
                                token = token,
                                endpoint = endpoint,
                            )
                        }

                        else -> MainEvent.CheckResult.EmptyCreds
                    }
                }
            },
        )
    }

    companion object {
        private const val MIN_DELAY_MS = 500L
    }
}
