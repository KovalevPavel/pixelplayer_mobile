package kovp.pixelplayer.initializer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kovp.pixelplayer.api_credentials.CredentialsRepository
import kovp.pixelplayer.core.language.AppLanguageManager
import kovp.pixelplayer.core.language.AppLanguageRepository
import kovp.pixelplayer.core_ui.launch

class InitializerViewModel(
    private val credentialsRepository: CredentialsRepository,
    private val languageManager: AppLanguageManager,
    private val languageRepository: AppLanguageRepository,
): ViewModel() {
    val eventsFlow: Flow<InitializerEvent> by lazy { _eventsFlow }
    private val _eventsFlow = MutableSharedFlow<InitializerEvent>()

    init {
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
                InitializerEvent.OpenLoginFlow
            }

            else -> {
                InitializerEvent.OpenMainFlow(token = token, endpoint = endpoint)
            }
        }

        _eventsFlow.emit(navigationEvent)
    }
}
