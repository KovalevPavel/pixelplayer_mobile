package kovp.pixelplayer.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kovp.pixelplayer.api_credentials.CredentialsRepository
import kovp.pixelplayer.api_login.LoginFlow
import kovp.pixelplayer.api_main_flow.MainFlow
import kovp.pixelplayer.core.language.AppLanguageManager
import kovp.pixelplayer.core.language.AppLanguageRepository
import kovp.pixelplayer.core_ui.launch

class MainViewModel(
    private val credentialsRepository: CredentialsRepository,
    private val languageManager: AppLanguageManager,
    private val languageRepository: AppLanguageRepository,
) : ViewModel() {
    val startDestinationFlow: StateFlow<Any?> by lazy { _startDestinationFlow }
    private val _startDestinationFlow = MutableStateFlow<Any?>(null)

    private var isInitialized = false

    private val languageInitializationFlow = MutableStateFlow(false)
    private val minDelayFlow = MutableStateFlow(false)
    private val credentialsStartDestinationFlow = MutableStateFlow<Any?>(null)

    init {
        subscribeOnReadinessFlow()
        initialize()
    }

    private fun subscribeOnReadinessFlow() {
        combine(
            languageInitializationFlow,
            minDelayFlow,
            credentialsStartDestinationFlow,
        ) { langIsInitialized, minDelayElapsed, startDest ->
            startDest.takeIf { langIsInitialized && minDelayElapsed }
        }
            .distinctUntilChanged()
            .onEach { startDest ->
                startDest ?: return@onEach
                _startDestinationFlow.update { startDest }
            }
            .launchIn(viewModelScope)
    }

    private fun initialize() {
        if (isInitialized) return

        launch(
            body = {
                listOf(
                    launch { launchMinDelay() },
                    launch { prepareLanguage() },
                    launch { checkCreds() },
                )
                    .joinAll()

                isInitialized = true
            },
        )
    }

    private suspend fun launchMinDelay() {
        delay(MIN_DELAY_MS)
        minDelayFlow.update { true }
    }

    private suspend fun prepareLanguage() {
        val selection = languageRepository.getSelection()

        if (!languageManager.supportsOverride || languageManager.isSelectionApplied(selection)) {
            languageInitializationFlow.update { true }
            return
        }

        languageManager.applySelection(selection)
        languageInitializationFlow.update { true }
    }

    private suspend fun checkCreds() {
        val endpoint = credentialsRepository.getEndpoint()
        val token = credentialsRepository.getToken()

        credentialsStartDestinationFlow.update {
            when {
                endpoint.isNullOrEmpty() || token.isNullOrEmpty() -> {
                    LoginFlow
                }

                else -> {
                    MainFlow(token = token, baseUrl = endpoint)
                }
            }
        }
    }

    companion object {
        private const val MIN_DELAY_MS = 1500L
    }
}
