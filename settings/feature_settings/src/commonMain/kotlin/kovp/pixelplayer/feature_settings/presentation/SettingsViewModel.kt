package kovp.pixelplayer.feature_settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kovp.pixelplayer.api_credentials.CredentialsRepository
import kovp.pixelplayer.core.build_config.BuildConfig
import kovp.pixelplayer.core.language.AppLanguageManager
import kovp.pixelplayer.core.language.AppLanguageRepository
import kovp.pixelplayer.core.language.LanguageSelection
import kovp.pixelplayer.core_player.Player
import kovp.pixelplayer.core_ui.UiText
import kovp.pixelplayer.core_ui.components.message_dialog.MessageDialogVs
import kovp.pixelplayer.core_ui.launch
import pixelplayer.core_ui.generated.resources.cancel
import pixelplayer.core_ui.generated.resources.ok
import pixelplayer.core_ui.generated.resources.Res as coreRes
import pixelplayer.feature_settings.generated.resources.Res
import pixelplayer.feature_settings.generated.resources.change_endpoint
import pixelplayer.feature_settings.generated.resources.logout_message

class SettingsViewModel(
    private val player: Player,
    private val credentialsRepository: CredentialsRepository,
    private val appLanguageRepository: AppLanguageRepository,
    private val appLanguageManager: AppLanguageManager,
    private val buildConfig: BuildConfig,
) : ViewModel() {
    val stateFlow: StateFlow<SettingsState> by lazy { _stateFlow }
    val settingsEvents: Flow<SettingsEvent> by lazy { _settingsEvents }

    private val _settingsEvents = MutableSharedFlow<SettingsEvent>()
    private val _stateFlow = MutableStateFlow<SettingsState>(SettingsState.Loading)

    init {
        fetchData()
    }

    fun handleAction(action: SettingsAction) {
        when (action) {
            SettingsAction.ChangeEndpoint -> changeEndpoint()
            is SettingsAction.ChangeLanguage -> changeLanguage(action.selection)
            SettingsAction.Logout -> logout()
            is SettingsAction.OnMessageDialogPrimaryClick -> handleDialogPrimaryAction(action.dialogId)
            SettingsAction.OnChangeLanguageClick -> showLanguagesDialog()
        }
    }

    private fun fetchData() {
        launch(
            body = {
                val login = credentialsRepository.getUsername().orEmpty()
                val endpoint = credentialsRepository.getEndpoint().orEmpty()
                val languageSelection = appLanguageRepository.getSelection()

                _stateFlow.update {
                    SettingsState.Data(
                        login = login,
                        endpoint = endpoint,
                        languageSelection = languageSelection,
                        deviceLanguage = appLanguageManager.resolveDeviceLanguage(),
                        isLanguagePickerVisible = appLanguageManager.supportsOverride,
                        isDemo = buildConfig.isDemoApp,
                        isProcessing = false,
                    )
                }
            },
        )
    }

    private fun logout() {
        MessageDialogVs(
            id = LOGOUT_DIALOG_ID,
            title = UiText.Resource(Res.string.logout_message),
            primaryAction = UiText.Resource(coreRes.string.ok),
            secondaryAction = UiText.Resource(coreRes.string.cancel),
        )
            .let(SettingsEvent::ShowMessageDialog)
            .let(::emitNewEvent)
    }

    private fun showLanguagesDialog() {
        SettingsEvent.ShowLanguagesDialog.let(::emitNewEvent)
    }

    private fun changeEndpoint() {
        MessageDialogVs(
            id = CHANGE_ENDPOINT_DIALOG_ID,
            title = UiText.Resource(Res.string.change_endpoint),
            primaryAction = UiText.Resource(coreRes.string.ok),
            secondaryAction = UiText.Resource(coreRes.string.cancel),
        )
            .let(SettingsEvent::ShowMessageDialog)
            .let(::emitNewEvent)
    }

    private fun handleDialogPrimaryAction(dialogId: String) {
        launch(
            body = {
                _stateFlow.update { st ->
                    (st as? SettingsState.Data)?.let { st.copy(isProcessing = true) } ?: st
                }

                when (dialogId) {
                    LOGOUT_DIALOG_ID -> {
                        player.clearPlayer()
                        clearUserData()
                        SettingsEvent.NavigateToLoginFlow.let(::emitNewEvent)
                    }

                    CHANGE_ENDPOINT_DIALOG_ID -> {
                        player.clearPlayer()
                        clearUserData()
                        credentialsRepository.saveEndpoint(null)
                        SettingsEvent.NavigateToLoginFlow.let(::emitNewEvent)
                    }
                }
            },
        )
    }

    private fun changeLanguage(selection: LanguageSelection) {
        launch(
            body = {
                appLanguageRepository.setSelection(selection)
                appLanguageManager.applySelection(selection)
                _stateFlow.update { st ->
                    (st as? SettingsState.Data)?.copy(
                        languageSelection = selection,
                        deviceLanguage = appLanguageManager.resolveDeviceLanguage(),
                    )
                        ?: st
                }
            },
        )
    }

    private fun emitNewEvent(newEvent: SettingsEvent) {
        viewModelScope.launch { _settingsEvents.emit(newEvent) }
    }

    private suspend fun clearUserData() {
        credentialsRepository.saveUsername(null)
        credentialsRepository.saveToken(null)
    }

    companion object {
        private const val LOGOUT_DIALOG_ID = "LOGOUT_DIALOG_ID"
        private const val CHANGE_ENDPOINT_DIALOG_ID = "CHANGE_ENDPOINT_DIALOG_ID"
    }
}
