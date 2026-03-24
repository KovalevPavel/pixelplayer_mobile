package kovp.pixelplayer.core_ui.components.message_dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import kovp.pixelplayer.core_design.pixelTypography
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_design.pixelColors
import org.jetbrains.compose.resources.stringResource
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
        scrimColor = pixelColors.scrim.copy(alpha = .2f),
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
                ?.takeUnless(MessageDialogVs.Field::isEmpty)
                ?.let {
                    Text(
                        text = it.value,
                        style = pixelTypography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                }

            viewState.message
                ?.takeUnless(MessageDialogVs.Field::isEmpty)
                ?.let {
                    Text(
                        text = it.value,
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
                        text = stringResource(viewState.primaryAction),
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
                            Text(text = stringResource(it))
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
            primaryAction = Res.string.ok,
        ),
        MessageDialogVs(
            message = MessageDialogVs.Field.Text("Simple message"),
            primaryAction = Res.string.ok,
        ),
        MessageDialogVs(
            title = MessageDialogVs.Field.Text("Message dialog title"),
            message = MessageDialogVs.Field.Text("Simple message"),
            primaryAction = Res.string.ok,
            secondaryAction = Res.string.cancel,
        ),
        MessageDialogVs(
            title = MessageDialogVs.Field.Text("Message dialog title ".repeat(3).trim()),
            message = MessageDialogVs.Field.Text("Simple message ".repeat(5).trim()),
            primaryAction = Res.string.ok,
        ),
        MessageDialogVs(
            title = MessageDialogVs.Field.Text("Message dialog title ".repeat(3).trim()),
            primaryAction = Res.string.ok,
        ),
    )
}
