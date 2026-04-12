package kovp.pixelplayer.feature_main_flow.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kovp.pixelplayer.api_storage.Preferences
import kovp.pixelplayer.core_ui.launch

class MainFlowViewModel(
    private val preferences: Preferences,
) : ViewModel() {
    val eventsFlow: Flow<MainFlowEvent> by lazy { _eventsFlow }
    private val _eventsFlow = MutableSharedFlow<MainFlowEvent>()

    fun handleAction(action: MainFlowAction) {
        when (action) {
            MainFlowAction.CheckTestAccountNotice -> checkTestAccountNotice()
        }
    }

    private fun checkTestAccountNotice() {
        launch(
            body = {
                val isNoticeShown = preferences.getString(TEST_ACCOUNT_NOTICE_SHOWN_KEY) != null

                if (!isNoticeShown) {
                    delay(TEST_ACCOUNT_DELAY_MS)
                    _eventsFlow.emit(MainFlowEvent.ShowTestAccountNotice)
                    dismissTestAccountNotice()
                }
            },
        )
    }

    private fun dismissTestAccountNotice() {
        launch(
            body = {
                preferences.updateValue(
                    key = TEST_ACCOUNT_NOTICE_SHOWN_KEY,
                    value = TEST_ACCOUNT_NOTICE_SHOWN_VALUE,
                )
            },
        )
    }

    private companion object {
        private const val TEST_ACCOUNT_NOTICE_SHOWN_KEY = "test_account_notice_shown"
        private const val TEST_ACCOUNT_NOTICE_SHOWN_VALUE = "1"
        private const val TEST_ACCOUNT_DELAY_MS = 1000L
    }
}
