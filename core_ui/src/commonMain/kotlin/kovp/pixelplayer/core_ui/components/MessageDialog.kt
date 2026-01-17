package kovp.pixelplayer.core_ui.components

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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_design.AppTypography
import kovp.pixelplayer.core_design.scrimLight

@Immutable
data class MessageDialogVs(
    val title: String? = null,
    val message: String? = null,
    val primaryAction: String,
    val secondaryAction: String? = null,
) {
    val isEmpty = title.isNullOrEmpty() && message.isNullOrEmpty()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDialog(
    viewState: MessageDialogVs?,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onPrimaryClick: () -> Unit = {},
    onSecondaryClick: () -> Unit = {},
) {
    if (viewState == null || viewState.isEmpty) {
        return
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (isVisible) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight(),
            scrimColor = scrimLight.copy(alpha = .2f),
            sheetState = sheetState,
            onDismissRequest = onDismiss,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                viewState.title
                    ?.takeUnless(String::isEmpty)
                    ?.let {
                        Text(
                            text = it,
                            style = AppTypography.titleMedium,
                            textAlign = TextAlign.Center,
                        )
                    }

                viewState.message
                    ?.takeUnless(String::isEmpty)
                    ?.let {
                        Text(
                            text = it,
                            style = AppTypography.bodyLarge,
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
                                onDismiss()
                            }
                        },
                    ) {
                        Text(text = viewState.primaryAction)
                    }

                    viewState.secondaryAction
                        ?.takeUnless(String::isEmpty)
                        ?.let {
                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    scope.launch {
                                        sheetState.hide()
                                        onSecondaryClick()
                                        onDismiss()
                                    }
                                },
                            ) {
                                Text(text = it)
                            }
                        }
                }
            }
        }
    }
}

@AppPreview
@Composable
private fun MessageDialogPreview(
    @PreviewParameter(ErrorVsProvider::class) viewState: MessageDialogVs,
) {
    AppTheme {
        MessageDialog(
            viewState = viewState,
            isVisible = true,
            onDismiss = {},
        )
    }
}

private class ErrorVsProvider : PreviewParameterProvider<MessageDialogVs> {
    override val values: Sequence<MessageDialogVs> = sequenceOf(
        MessageDialogVs(
            primaryAction = "Ok"
        ),
        MessageDialogVs(
            message = "Simple message",
            primaryAction = "Ok",
        ),
        MessageDialogVs(
            title = "Message dialog title",
            message = "Simple message",
            primaryAction = "Ok",
            secondaryAction = "Cancel"
        ),
        MessageDialogVs(
            title = "Message dialog title ".repeat(3).trim(),
            message = "Simple message ".repeat(5).trim(),
            primaryAction = "Ok",
        ),
        MessageDialogVs(
            title = "Message dialog title ".repeat(3).trim(),
            primaryAction = "Ok",
        ),
    )
}
