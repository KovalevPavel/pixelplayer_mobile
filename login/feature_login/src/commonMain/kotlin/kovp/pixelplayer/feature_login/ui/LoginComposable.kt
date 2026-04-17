package kovp.pixelplayer.feature_login.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kovp.pixelplayer.core_ui.CollectWithLifecycle
import kovp.pixelplayer.core_ui.components.FullScreenLoader
import kovp.pixelplayer.core_ui.components.message_dialog.MessageDialog
import kovp.pixelplayer.core_ui.components.message_dialog.MessageDialogVs
import kovp.pixelplayer.feature_login.LoginAction
import kovp.pixelplayer.feature_login.LoginEvent
import kovp.pixelplayer.feature_login.LoginState
import kovp.pixelplayer.feature_login.LoginViewModel
import org.koin.core.scope.Scope

@Composable
fun LoginComposable(
    scope: Scope,
    navigateToMainFlow: (token: String, endpoint: String) -> Unit,
) {
    val viewModel: LoginViewModel = remember { scope.get() }
    val state by viewModel.screenState.collectAsState()

    when (val st = state) {
        is LoginState.Data -> {
            DataContent(
                eventsFlow = viewModel.eventsFlow,
                state = st,
                handleAction = viewModel::handleAction,
                navigateToMainFlow = navigateToMainFlow,
            )
        }

        LoginState.Init -> {
            // empty
        }
    }

    LaunchedEffect(scope) {
        LoginAction.CheckCreds.let(viewModel::handleAction)
    }
}

@Composable
private fun DataContent(
    eventsFlow: Flow<LoginEvent>,
    state: LoginState.Data,
    handleAction: (LoginAction) -> Unit,
    navigateToMainFlow: (token: String, endpoint: String) -> Unit,
) {
    var isLoaderVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var messageDialogVs: MessageDialogVs? by remember { mutableStateOf(null) }
    val pagerState = rememberPagerState(
        initialPage = state.initPage,
        pageCount = { state.pages.size },
    )

    val controller = LocalSoftwareKeyboardController.current

    eventsFlow.CollectWithLifecycle { event ->
        when (event) {
            is LoginEvent.ShowError -> {
                coroutineScope.launch {
                    controller?.hide()
                    messageDialogVs = event.viewState
                }
            }

            is LoginEvent.NavigateToMainFlow -> {
                navigateToMainFlow(event.token, event.endpoint)
            }

            is LoginEvent.NavigateToStep -> {
                coroutineScope.launch { pagerState.animateScrollToPage(event.step) }
            }

            is LoginEvent.ShowLoader -> {
                isLoaderVisible = event.show
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { paddings ->
        HorizontalPager(
            modifier = Modifier
                .padding(paddings)
                .imePadding(),
            state = pagerState,
            userScrollEnabled = false,
        ) { index ->
            when (val page = state.pages[index]) {
                is LoginState.Credentials -> {
                    CredentialsScreen(handleAction = handleAction)
                }

                is LoginState.Endpoint -> {
                    EndpointScreen(
                        initEndpoint = page.initEndpoint,
                        handleAction = handleAction,
                    )
                }
            }
        }
    }

    if (isLoaderVisible) {
        FullScreenLoader()
    }

    messageDialogVs?.let {
        MessageDialog(
            viewState = it,
            removeFromComposition = { messageDialogVs = null },
        )
    }
}
