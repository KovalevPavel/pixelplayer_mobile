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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.feature_login.LoginAction
import org.jetbrains.compose.resources.stringResource
import pixelplayer.core_ui.generated.resources.change_server
import pixelplayer.core_ui.generated.resources.login
import pixelplayer.core_ui.generated.resources.username
import pixelplayer.feature_login.generated.resources.Res
import pixelplayer.feature_login.generated.resources.password
import pixelplayer.core_ui.generated.resources.Res as coreRes

@Composable
fun CredentialsScreen(
    handleAction: (LoginAction) -> Unit,
) {
    val controller = LocalSoftwareKeyboardController.current

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
                        Text(text = stringResource(coreRes.string.username))
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
                        Text(text = stringResource(Res.string.password))
                    },
                    visualTransformation = PasswordVisualTransformation(),
                )
            }

            Column {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = login.isNotEmpty() && password.isNotEmpty(),
                    onClick = {
                        controller?.hide()
                        handleAction(
                            LoginAction.Login(login = login, password = password),
                        )
                    },
                ) {
                    Text(text = stringResource(coreRes.string.login))
                }

                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        handleAction(LoginAction.ChangeEndpoint)
                    },
                ) {
                    Text(text = stringResource(coreRes.string.change_server))
                }
            }
        }
    }
}

@AppPreview
@Composable
private fun CredentialsPreview() {
    AppTheme {
        CredentialsScreen(
            handleAction = {},
        )
    }
}
