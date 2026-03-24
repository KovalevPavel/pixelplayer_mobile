package kovp.pixelplayer.feature_settings.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_design.pixelColors
import kovp.pixelplayer.core_design.pixelTypography
import kovp.pixelplayer.core_ui.CollectWithLifecycle
import kovp.pixelplayer.core_ui.components.FullScreenLoader
import kovp.pixelplayer.core_ui.components.message_dialog.MessageDialog
import kovp.pixelplayer.core_ui.components.message_dialog.MessageDialogVs
import kovp.pixelplayer.feature_settings.presentation.SettingsAction
import kovp.pixelplayer.feature_settings.presentation.SettingsEvent
import kovp.pixelplayer.feature_settings.presentation.SettingsState
import kovp.pixelplayer.feature_settings.presentation.SettingsViewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.scope.Scope
import pixelplayer.core_ui.generated.resources.Res as coreRes
import pixelplayer.core_ui.generated.resources.change_server
import pixelplayer.core_ui.generated.resources.endpoint
import pixelplayer.core_ui.generated.resources.logout
import pixelplayer.feature_settings.generated.resources.Res
import pixelplayer.feature_settings.generated.resources.username

@Composable
fun SettingsScreenComposable(
    scope: Scope,
    onLogout: () -> Unit,
) {
    val viewModel: SettingsViewModel = remember { scope.get() }
    val viewState by viewModel.stateFlow.collectAsState()

    var dialogVs: MessageDialogVs? by remember { mutableStateOf(null) }

    viewModel.settingsEvents.CollectWithLifecycle { event ->
        when (event) {
            SettingsEvent.NavigateToLoginFlow -> {
                scope.close()
                onLogout()
            }
            is SettingsEvent.ShowMessageDialog -> dialogVs = event.viewState
        }
    }

    SettingScreenContent(
        viewState = viewState,
        onAction = viewModel::handleAction,
    )

    dialogVs?.let {
        MessageDialog(
            viewState = it,
            removeFromComposition = { dialogVs = null },
            onPrimaryClick = {
                SettingsAction.OnMessageDialogPrimaryClick(it.id)
                    .let(viewModel::handleAction)
            },
        )
    }
}

@Composable
private fun SettingScreenContent(
    viewState: SettingsState,
    onAction: (SettingsAction) -> Unit,
) {
    AnimatedContent(
        targetState = viewState,
        contentKey = { it::class },
    ) { st ->
        when (st) {
            is SettingsState.Data -> {
                SettingsScreenData(viewState = st, onAction = onAction)
            }

            SettingsState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

}

@Composable
private fun SettingsScreenData(
    viewState: SettingsState.Data,
    onAction: (SettingsAction) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 64.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            TextData(
                title = coreRes.string.endpoint,
                text = viewState.endpoint,
            )

            TextData(
                title = Res.string.username,
                text = viewState.login,
            )

            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 64.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    onClick = { onAction(SettingsAction.ChangeEndpoint) },
                ) {
                    Text(text = stringResource(coreRes.string.change_server))
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    onClick = { onAction(SettingsAction.Logout) },
                ) {
                    Text(text = stringResource(coreRes.string.logout))
                }
            }
        }

        if (viewState.isProcessing) {
            FullScreenLoader()
        }
    }
}

@Composable
private fun TextData(title: StringResource, text: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(title),
            style = pixelTypography.titleMedium,
            color = pixelColors.onBackground,
        )

        Text(
            text = text,
            style = pixelTypography.bodyMedium,
            color = pixelColors.onSurfaceVariant,
        )
    }
}

@AppPreview
@Composable
private fun SettingsScreenPreview(
    @PreviewParameter(SettingsStateProvider::class) viewState: SettingsState,
) {
    AppTheme {
        SettingScreenContent(
            viewState = viewState,
            onAction = {},
        )
    }
}

private class SettingsStateProvider : PreviewParameterProvider<SettingsState> {
    override val values: Sequence<SettingsState> = sequenceOf(
        SettingsState.Loading,
        SettingsState.Data(
            login = "unknown_user",
            endpoint = "https://www.example.com",
            isProcessing = false,
        ),
        SettingsState.Data(
            login = "unknown_user",
            endpoint = "https://www.example.com",
            isProcessing = true,
        ),
    )
}
