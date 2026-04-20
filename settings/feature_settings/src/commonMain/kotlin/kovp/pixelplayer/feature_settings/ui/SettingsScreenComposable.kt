package kovp.pixelplayer.feature_settings.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kovp.pixelplayer.core.language.AppLanguage
import kovp.pixelplayer.core.language.LanguageSelection
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_design.pixelColors
import kovp.pixelplayer.core_design.pixelTypography
import kovp.pixelplayer.core_ui.CollectWithLifecycle
import kovp.pixelplayer.core_ui.components.FullScreenLoader
import kovp.pixelplayer.core_ui.components.content_dialog.ContentDialog
import kovp.pixelplayer.core_ui.components.message_dialog.MessageDialog
import kovp.pixelplayer.core_ui.components.message_dialog.MessageDialogVs
import kovp.pixelplayer.feature_settings.presentation.SettingsAction
import kovp.pixelplayer.feature_settings.presentation.SettingsEvent
import kovp.pixelplayer.feature_settings.presentation.SettingsState
import kovp.pixelplayer.feature_settings.presentation.SettingsViewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.scope.Scope
import pixelplayer.core_ui.generated.resources.change_language
import pixelplayer.core_ui.generated.resources.change_server
import pixelplayer.core_ui.generated.resources.endpoint
import pixelplayer.core_ui.generated.resources.language
import pixelplayer.core_ui.generated.resources.logout
import pixelplayer.core_ui.generated.resources.system
import pixelplayer.feature_settings.generated.resources.Res
import pixelplayer.feature_settings.generated.resources.license_info
import pixelplayer.feature_settings.generated.resources.license_url
import pixelplayer.feature_settings.generated.resources.username
import pixelplayer.core_ui.generated.resources.Res as coreRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenComposable(
    scope: Scope,
    onLogout: () -> Unit,
) {
    val viewModel: SettingsViewModel = remember { scope.get() }
    val viewState by viewModel.stateFlow.collectAsState()

    var dialogVs: MessageDialogVs? by remember { mutableStateOf(null) }
    var isLanguageDialogVisible by remember { mutableStateOf(false) }

    viewModel.settingsEvents.CollectWithLifecycle { event ->
        when (event) {
            SettingsEvent.NavigateToLoginFlow -> {
                scope.close()
                onLogout()
            }

            is SettingsEvent.ShowMessageDialog -> {
                dialogVs = event.viewState
            }

            is SettingsEvent.ShowLanguagesDialog -> {
                isLanguageDialogVisible = true
            }
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

    if (isLanguageDialogVisible && viewState is SettingsState.Data) {
        ContentDialog(
            removeFromComposition = { isLanguageDialogVisible = false },
        ) { scope, state ->
            LanguageDialog(
                languageSelection = (viewState as SettingsState.Data).languageSelection,
                deviceLanguage = (viewState as SettingsState.Data).deviceLanguage,
                onAction = {
                    scope.launch {
                        state.hide()
                        viewModel.handleAction(it)
                        isLanguageDialogVisible = false
                    }
                },
            )
        }
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
    val uriHandler = LocalUriHandler.current

    Scaffold(
        modifier = Modifier.fillMaxSize().padding(all = 16.dp),
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
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

            if (viewState.isLanguagePickerVisible) {
                LanguageData(
                    languageSelection = viewState.languageSelection,
                    deviceLanguage = viewState.deviceLanguage,
                    onAction = onAction,
                )
            }

            if (viewState.isDemo) {
                val licenseText = buildAnnotatedString {
                    stringResource(Res.string.license_info).let(::appendLine)
                    val link = stringResource(Res.string.license_url)
                    withLink(
                        LinkAnnotation.Url(
                            url = link,
                            styles = TextLinkStyles(style = SpanStyle(color = Color(0xff3474eb))),
                            linkInteractionListener = { uriHandler.openUri(link) },
                        )
                    ) {
                        link.let(::appendLine)
                    }
                }

                Text(
                    text = licenseText,
                    style = pixelTypography.bodyLarge,
                    color = pixelColors.onBackground,
                )
            }

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
    }

    if (viewState.isProcessing) {
        FullScreenLoader()
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

@Composable
private fun LanguageData(
    languageSelection: LanguageSelection,
    deviceLanguage: AppLanguage,
    onAction: (SettingsAction) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(coreRes.string.language),
            style = pixelTypography.titleMedium,
            color = pixelColors.onBackground,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = when (languageSelection) {
                    LanguageSelection.System -> {
                        "${stringResource(coreRes.string.system)} (${deviceLanguage.toStringRes()})"
                    }

                    is LanguageSelection.Explicit -> {
                        languageSelection.language.toStringRes()
                    }
                },
                style = pixelTypography.bodyMedium,
                color = pixelColors.onSurfaceVariant,
            )

            OutlinedButton(
                enabled = true,
                onClick = { onAction(SettingsAction.OnChangeLanguageClick) },
            ) {
                Text(text = stringResource(coreRes.string.change_language))
            }
        }
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
            languageSelection = LanguageSelection.System,
            deviceLanguage = AppLanguage.English,
            isLanguagePickerVisible = true,
            isDemo = false,
            isProcessing = false,
        ),
        SettingsState.Data(
            login = "unknown_user",
            endpoint = "https://www.example.com",
            languageSelection = LanguageSelection.System,
            deviceLanguage = AppLanguage.English,
            isLanguagePickerVisible = true,
            isDemo = true,
            isProcessing = false,
        ),
        SettingsState.Data(
            login = "unknown_user",
            endpoint = "https://www.example.com",
            languageSelection = LanguageSelection.Explicit(AppLanguage.German),
            deviceLanguage = AppLanguage.German,
            isLanguagePickerVisible = true,
            isDemo = false,
            isProcessing = true,
        ),
    )
}
