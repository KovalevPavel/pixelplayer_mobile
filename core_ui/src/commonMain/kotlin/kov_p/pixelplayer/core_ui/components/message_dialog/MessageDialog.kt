package kov_p.pixelplayer.core_ui.components.message_dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kov_p.pixelplayer.core_design.pixelTypography
import kov_p.pixelplayer.core_design.AppPreview
import kov_p.pixelplayer.core_design.AppTheme
import kov_p.pixelplayer.core_design.pixelColors
import kov_p.pixelplayer.core_ui.UiText
import kov_p.pixelplayer.core_ui.asString
import pixelplayer.core_ui.generated.resources.Res
import pixelplayer.core_ui.generated.resources.cancel
import pixelplayer.core_ui.generated.resources.ok

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDialog(
    viewState: MessageDialogVs?,
    onPrimaryClick: () -> Unit = {},
    onSecondaryClick: () -> Unit = {},
    removeFromComposition: () -> Unit,
) {
    if (viewState == null) {
        return
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        modifier = Modifier.fillMaxWidth()
            .wrapContentHeight(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .7f),
            )
        },
        scrimColor = pixelColors.scrim.copy(alpha = .32f),
        sheetState = sheetState,
        onDismissRequest = removeFromComposition,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            viewState.title
                ?.takeUnless(UiText::isEmpty)
                ?.let {
                    Text(
                        text = it.asString(),
                        style = pixelTypography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                }

            viewState.message
                ?.takeUnless(UiText::isEmpty)
                ?.let {
                    Text(
                        text = it.asString(),
                        style = pixelTypography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }

            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onPrimaryClick()
                            removeFromComposition()
                        }
                    },
                ) {
                    Text(
                        text = viewState.primaryAction.asString(),
                    )
                }

                viewState.secondaryAction
                    ?.let {
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                scope.launch {
                                    sheetState.hide()
                                    onSecondaryClick()
                                    removeFromComposition()
                                }
                            },
                        ) {
                            Text(text = it.asString())
                        }
                    }
            }
        }
    }
}

@AppPreview
@Composable
private fun MessageDialogPreview(
    @PreviewParameter(MessageDialogVsProvider::class) viewState: MessageDialogVs,
) {
    AppTheme {
        MessageDialog(
            viewState = viewState,
            removeFromComposition = {},
        )
    }
}

private class MessageDialogVsProvider : PreviewParameterProvider<MessageDialogVs> {
    override val values: Sequence<MessageDialogVs> = sequenceOf(
        MessageDialogVs(
            primaryAction = UiText.Resource(Res.string.ok),
        ),
        MessageDialogVs(
            message = UiText.Dynamic("Simple message"),
            primaryAction = UiText.Resource(Res.string.ok),
        ),
        MessageDialogVs(
            title = UiText.Dynamic("Message dialog title"),
            message = UiText.Dynamic("Simple message"),
            primaryAction = UiText.Resource(Res.string.ok),
            secondaryAction = UiText.Resource(Res.string.cancel),
        ),
        MessageDialogVs(
            title = UiText.Dynamic("Message dialog title ".repeat(3).trim()),
            message = UiText.Dynamic("Simple message ".repeat(5).trim()),
            primaryAction = UiText.Resource(Res.string.ok),
        ),
        MessageDialogVs(
            title = UiText.Dynamic("Message dialog title ".repeat(3).trim()),
            primaryAction = UiText.Resource(Res.string.ok),
        ),
    )
}
