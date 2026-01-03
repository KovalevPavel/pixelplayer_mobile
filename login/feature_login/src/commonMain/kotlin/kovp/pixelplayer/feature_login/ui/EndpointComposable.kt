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
import kovp.pixelplayer.core_ui.components.MessageDialog
import kovp.pixelplayer.core_ui.components.MessageDialogVs
import kovp.pixelplayer.feature_login.LoginAction
import kovp.pixelplayer.feature_login.LoginEvent
import kovp.pixelplayer.feature_login.LoginViewModel
import org.koin.core.scope.Scope

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

    MessageDialog(
        viewState = messageDialogVs,
        isVisible = messageDialogVs != null,
        onDismiss = { messageDialogVs = null },
    )
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
                placeholder = { Text("https://") },
                label = { Text("Enter server's endpoint") },
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = endpoint.isNotEmpty() && !isLoaderVisible,
                onClick = {
                    handleAction(LoginAction.CheckEndpoint(endpoint))
                },
            ) {
                Text("Check")
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
