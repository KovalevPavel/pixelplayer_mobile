package kov_p.pixelplayer.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
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
import kov_p.pixelplayer.api_credentials.CredentialsRepository
import kov_p.pixelplayer.api_login.LoginFlow
import kov_p.pixelplayer.api_main_flow.MainFlow
import kov_p.pixelplayer.core.language.AppLanguageManager
import kov_p.pixelplayer.core.language.AppLanguageRepository
import kov_p.pixelplayer.core_ui.launch

class MainViewModel(
    private val credentialsRepository: CredentialsRepository,
    private val languageManager: AppLanguageManager,
    private val languageRepository: AppLanguageRepository,
) : ViewModel() {
    val startDestinationFlow: StateFlow<Any?> by lazy { _startDestinationFlow }
    private val _startDestinationFlow = MutableStateFlow<Any?>(null)

    private var isInitialized = false

    private val languageInitializationFlow = MutableStateFlow(true)
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
//                    launch { prepareLanguage() },
                    launch { checkCreds() },
                )
                    .joinAll()

                isInitialized = true
            },
            onFailure = {
                completeStartupWithFallback()
            },
        )
    }

    private suspend fun launchMinDelay() {
        delay(MIN_DELAY_MS)
        minDelayFlow.update { true }
    }

    private suspend fun prepareLanguage() {
        try {
            val selection = languageRepository.getSelection()

            if (!languageManager.supportsOverride || languageManager.isSelectionApplied(selection)) {
                languageInitializationFlow.update { true }
                return
            }

            languageManager.applySelection(selection)
            languageInitializationFlow.update { true }
        } catch (error: CancellationException) {
            throw error
        } catch (_: Throwable) {
            languageInitializationFlow.update { true }
        }
    }

    private suspend fun checkCreds() {
        try {
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
        } catch (error: CancellationException) {
            throw error
        } catch (_: Throwable) {
            credentialsStartDestinationFlow.update { LoginFlow }
        }
    }

    private fun completeStartupWithFallback() {
        languageInitializationFlow.update { true }
        minDelayFlow.update { true }
        credentialsStartDestinationFlow.update { LoginFlow }
    }

    companion object {
        private const val MIN_DELAY_MS = 1500L
    }
}
