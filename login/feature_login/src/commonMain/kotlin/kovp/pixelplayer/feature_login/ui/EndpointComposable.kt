package kovp.pixelplayer.feature_login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_ui.CollectWithLifecycle
import kovp.pixelplayer.core_ui.components.FullScreenLoader
import kovp.pixelplayer.core_ui.components.message_dialog.MessageDialog
import kovp.pixelplayer.core_ui.components.message_dialog.MessageDialogVs
import kovp.pixelplayer.feature_login.LoginAction
import kovp.pixelplayer.feature_login.LoginEvent
import kovp.pixelplayer.feature_login.LoginViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.core.scope.Scope
import pixelplayer.feature_login.generated.resources.Res
import pixelplayer.feature_login.generated.resources.check
import pixelplayer.feature_login.generated.resources.endpoint_placeholder
import pixelplayer.feature_login.generated.resources.enter_server_endpoint

@Composable
fun EndpointComposable(
    scope: Scope,
    onEndpointSaved: () -> Unit,
) {
    var isLoaderVisible by remember { mutableStateOf(false) }

    val viewModel: LoginViewModel = remember { scope.get() }
    var messageDialogVs: MessageDialogVs? by remember { mutableStateOf(null) }

    viewModel.eventsFlow.CollectWithLifecycle { event ->
        isLoaderVisible = false

        when (event) {
            is LoginEvent.ShowError -> {
                messageDialogVs = event.viewState
            }

            is LoginEvent.NavigateNext -> {
                onEndpointSaved()
            }

            is LoginEvent.NavigatePrevious -> {
                // do nothing
            }
        }
    }

    EndpointScreen(
        isLoaderVisible = isLoaderVisible,
        handleAction = {
            isLoaderVisible = true
            viewModel.handleAction(it)
        },
    )

    messageDialogVs?.let {
        MessageDialog(
            viewState = it,
            removeFromComposition = { messageDialogVs = null },
        )
    }
}

@Composable
private fun EndpointScreen(
    isLoaderVisible: Boolean,
    handleAction: (LoginAction) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.width(IntrinsicSize.Max),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            var endpoint by rememberSaveable { mutableStateOf("") }

            OutlinedTextField(
                value = endpoint,
                onValueChange = { endpoint = it },
                singleLine = true,
                placeholder = { Text(stringResource(Res.string.endpoint_placeholder)) },
                label = { Text(stringResource(Res.string.enter_server_endpoint)) },
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = endpoint.isNotEmpty() && !isLoaderVisible,
                onClick = {
                    handleAction(LoginAction.CheckEndpoint(endpoint))
                },
            ) {
                Text(stringResource(Res.string.check))
            }
        }

        if (isLoaderVisible) {
            FullScreenLoader()
        }
    }
}

@AppPreview
@Composable
private fun EndpointPreview() {
    var isLoaderVisible by remember { mutableStateOf(false) }

    AppTheme {
        EndpointScreen(
            isLoaderVisible = isLoaderVisible,
            handleAction = { isLoaderVisible = true }
        )
    }
}
