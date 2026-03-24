package kovp.pixelplayer.feature_settings.presentation

sealed interface SettingsState {
    data object Loading : SettingsState

    data class Data(
        val login: String,
        val endpoint: String,
        val isProcessing: Boolean,
    ) : SettingsState
}
