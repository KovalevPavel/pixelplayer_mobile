package kovp.pixelplayer.feature_login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
fun CredentialsComposable(
    scope: Scope,
    onTokenSaved: () -> Unit,
    onChangeEndpoint: () -> Unit,
) {
    val viewModel: LoginViewModel = remember { scope.get() }
    var isLoadingVisible by remember { mutableStateOf(false) }
    var errorVs: MessageDialogVs? by remember { mutableStateOf(null) }

    viewModel.eventsFlow.CollectWithLifecycle { event ->
        isLoadingVisible = false
        when (event) {
            is LoginEvent.NavigateNext -> onTokenSaved()
            is LoginEvent.NavigatePrevious -> onChangeEndpoint()
            is LoginEvent.ShowError -> errorVs = event.viewState
        }
    }

    CredentialsScreen(
        isLoadingVisible = isLoadingVisible,
        handleAction = {
            isLoadingVisible = true
            viewModel.handleAction(it)
        },
    )

    MessageDialog(
        viewState = errorVs,
        isVisible = errorVs != null,
        onDismiss = { errorVs = null },
    )
}

@Composable
private fun CredentialsScreen(
    isLoadingVisible: Boolean,
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
            var login by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }

            Column {
                OutlinedTextField(
                    value = login,
                    singleLine = true,
                    onValueChange = { login = it },
                    label = {
                        Text("login")
                    },
                )

                OutlinedTextField(
                    value = password,
                    singleLine = true,
                    onValueChange = { password = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                    ),
                    label = {
                        Text("password")
                    },
                    visualTransformation = PasswordVisualTransformation(),
                )
            }

            Column {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = login.isNotEmpty() && password.isNotEmpty(),
                    onClick = {
                        handleAction(
                            LoginAction.Login(login = login, password = password),
                        )
                    },
                ) {
                    Text(text = "Login")
                }

                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        handleAction(LoginAction.ChangeEndpoint)
                    },
                ) {
                    Text(text = "Change endpoint")
                }
            }
        }

        if (isLoadingVisible) {
            FullScreenLoader()
        }
    }
}

@AppPreview
@Composable
private fun CredentialsPreview() {
    var isLoadingVisible by remember { mutableStateOf(false) }

    AppTheme {
        CredentialsScreen(
            isLoadingVisible = isLoadingVisible,
            handleAction = {},
        )
    }
}
