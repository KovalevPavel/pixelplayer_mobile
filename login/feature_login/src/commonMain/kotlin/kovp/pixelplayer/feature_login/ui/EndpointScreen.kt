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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_ui.components.PrivacyPolicyLink
import kovp.pixelplayer.feature_login.LoginAction
import org.jetbrains.compose.resources.stringResource
import pixelplayer.feature_login.generated.resources.Res
import pixelplayer.feature_login.generated.resources.check
import pixelplayer.feature_login.generated.resources.endpoint_placeholder
import pixelplayer.feature_login.generated.resources.enter_server_endpoint

@Composable
fun EndpointScreen(
    initEndpoint: String,
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
            var endpoint by rememberSaveable { mutableStateOf(initEndpoint) }

            OutlinedTextField(
                value = endpoint,
                onValueChange = { endpoint = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                ),
                placeholder = { Text(text = stringResource(Res.string.endpoint_placeholder)) },
                label = { Text(text = stringResource(Res.string.enter_server_endpoint)) },
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = endpoint.isNotEmpty(),
                onClick = {
                    handleAction(LoginAction.CheckEndpoint(endpoint))
                },
            ) {
                Text(text = stringResource(Res.string.check))
            }

            PrivacyPolicyLink(modifier = Modifier.fillMaxWidth())
        }
    }
}

@AppPreview
@Composable
private fun EndpointPreview() {
    AppTheme {
        EndpointScreen(
            initEndpoint = "",
            handleAction = {}
        )
    }
}
