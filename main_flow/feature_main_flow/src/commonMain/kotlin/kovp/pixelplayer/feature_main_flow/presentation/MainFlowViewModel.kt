package kovp.pixelplayer.feature_main_flow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kovp.pixelplayer.api_storage.Preferences
import kovp.pixelplayer.core.build_config.BuildConfig
import kovp.pixelplayer.core_ui.UiText
import kovp.pixelplayer.core_ui.components.message_dialog.MessageDialogVs
import kovp.pixelplayer.core_ui.launch
import pixelplayer.core_ui.generated.resources.ok
import pixelplayer.feature_main_flow.generated.resources.Res
import pixelplayer.feature_main_flow.generated.resources.demo_app_notice_message

import pixelplayer.core_ui.generated.resources.Res as coreRes

class MainFlowViewModel(
    private val preferences: Preferences,
    private val buildConfig: BuildConfig,
) : ViewModel() {
    val eventsFlow: Flow<MainFlowEvent> by lazy { _eventsFlow }
    private val _eventsFlow = MutableSharedFlow<MainFlowEvent>()

    fun handleAction(action: MainFlowAction) {
        when (action) {
            MainFlowAction.CheckDemoAppNotice -> checkDemoAppNotice()
        }
    }

    private fun checkDemoAppNotice() {
        launch(
            body = {
                if (!buildConfig.isDemoApp) {
                    return@launch
                }

                val isNoticeShown = preferences.getString(DEMO_APP_NOTICE_SHOWN_KEY) != null

                if (!isNoticeShown) {
                    delay(EMIT_DELAY_MS)
                    MessageDialogVs(
                        title = UiText.Resource(Res.string.demo_app_notice_message),
                        primaryAction = UiText.Resource(coreRes.string.ok),
                    )
                        .let(MainFlowEvent::ShowMessageDialog)
                        .let(::emitNewEvent)

                    dismissTestAccountNotice()
                }
            },
            onFailure = {
                MessageDialogVs(
                    title = UiText.Dynamic(it.message),
                    primaryAction = UiText.Resource(coreRes.string.ok),
                )
                    .let(MainFlowEvent::ShowMessageDialog)
                    .let(::emitNewEvent)
            }
        )
    }

    private fun dismissTestAccountNotice() {
        launch(
            body = {
                preferences.updateValue(
                    key = DEMO_APP_NOTICE_SHOWN_KEY,
                    value = DEMO_APP_NOTICE_SHOWN_VALUE,
                )
            },
        )
    }

    private fun emitNewEvent(event: MainFlowEvent) {
        viewModelScope.launch { _eventsFlow.emit(event) }
    }

    private companion object {
        private const val DEMO_APP_NOTICE_SHOWN_KEY = "demo_app_notice_shown"
        private const val DEMO_APP_NOTICE_SHOWN_VALUE = "1"
        private const val EMIT_DELAY_MS = 1000L
    }
}
