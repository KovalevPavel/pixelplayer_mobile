package kov_p.pixelplayer.core_ui.components.error_view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kov_p.pixelplayer.core_design.AppPreview
import kov_p.pixelplayer.core_design.AppTheme
import kov_p.pixelplayer.core_design.pixelColors
import kov_p.pixelplayer.core_design.pixelTypography
import kov_p.pixelplayer.core_ui.UiText
import kov_p.pixelplayer.core_ui.asString
import pixelplayer.core_ui.generated.resources.Res
import pixelplayer.core_ui.generated.resources.retry

@Composable
fun ErrorView(
    modifier: Modifier = Modifier,
    viewState: ErrorVs,
    onActionClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = viewState.message.asString(),
            style = pixelTypography.bodyMedium,
            color = pixelColors.onBackground,
            textAlign = TextAlign.Center,
        )

        OutlinedButton(
            onClick = onActionClick,
        ) {
            Text(
                text = viewState.action.asString(),
            )
        }
    }
}

@AppPreview
@Composable
private fun ErrorViewPreview(
    @PreviewParameter(ErrorVsProvider::class) viewState: ErrorVs,
) {
    AppTheme {
        ErrorView(
            viewState = viewState,
            onActionClick = {},
        )
    }
}

private class ErrorVsProvider : PreviewParameterProvider<ErrorVs> {
    override val values: Sequence<ErrorVs> = sequenceOf(
        ErrorVs(
            message = UiText.Dynamic("Error message"),
            action = UiText.Resource(Res.string.retry),
        ),
        ErrorVs(
            message = UiText.Dynamic("Error message ".repeat(5).trim()),
            action = UiText.Resource(Res.string.retry),
        ),
    )
}
