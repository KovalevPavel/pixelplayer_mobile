package kovp.pixelplayer.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kovp.pixelplayer.api_credentials.CredentialsRepository
import kovp.pixelplayer.core.language.AppLanguageManager
import kovp.pixelplayer.core.language.AppLanguageRepository
import kovp.pixelplayer.core_ui.launch

class MainViewModel(
    private val credentialsRepository: CredentialsRepository,
    private val languageManager: AppLanguageManager,
    private val languageRepository: AppLanguageRepository,
) : ViewModel() {
    val eventsFlow: Flow<MainEvent> by lazy { eventsChannel.receiveAsFlow() }

    private val eventsChannel = Channel<MainEvent>(capacity = Channel.BUFFERED)
    private var isInitialized = false

    init {
        handleAction(MainAction.Initialize)
    }

    fun handleAction(action: MainAction) {
        when (action) {
            MainAction.Initialize -> initialize()
        }
    }

    private fun initialize() {
        if (isInitialized) return

        isInitialized = true
        launch(
            body = {
                prepareLanguage()
                checkCreds()
            },
        )
    }

    private suspend fun prepareLanguage() {
        val selection = languageRepository.getSelection()

        if (!languageManager.supportsOverride || languageManager.isSelectionApplied(selection)) {
            return
        }

        languageManager.applySelection(selection)
    }

    private suspend fun checkCreds() {
        val endpoint = credentialsRepository.getEndpoint()
        val token = credentialsRepository.getToken()

        val navigationEvent = when {
            endpoint.isNullOrEmpty() || token.isNullOrEmpty() -> {
                MainEvent.OpenLoginFlow
            }

            else -> {
                MainEvent.OpenMainFlow(token = token, endpoint = endpoint)
            }
        }

        eventsChannel.send(navigationEvent)
        eventsChannel.send(MainEvent.SplashChecksPassed)
    }
}
