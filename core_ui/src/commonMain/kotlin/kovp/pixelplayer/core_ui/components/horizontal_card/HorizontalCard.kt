package kovp.pixelplayer.core_ui.components.horizontal_card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.pixelTypography
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_design.pixelColors
import kovp.pixelplayer.core_ui.UiText
import kovp.pixelplayer.core_ui.asString
import kovp.pixelplayer.core_ui.components.image.PixelImage
import kovp.pixelplayer.core_ui.components.playing_icon.PlayingIcon

@Composable
fun HorizontalCard(
    modifier: Modifier = Modifier,
    viewState: HorizontalCardVs,
    trailingIcon: @Composable BoxScope.() -> Unit = {},
    border: @Composable () -> BorderStroke = {
        BorderStroke(width = 1.dp, color = pixelColors.onSurfaceVariant)
    },
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(10.dp),
        border = border(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .padding(end = 8.dp)
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            viewState.imageUrl?.let {
                PixelImage(
                    modifier = Modifier.size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    url = viewState.imageUrl,
                )
            }

            Column(
                modifier = Modifier.fillMaxHeight().weight(1f),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(
                    text = viewState.title,
                    style = pixelTypography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = viewState.description.asString(),
                    style = pixelTypography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                trailingIcon()
            }
        }
    }
}

@Preview(name = "No icon")
@Composable
private fun HorizontalCardPreview(
    @PreviewParameter(HorizontalVsProvider::class) vs: HorizontalCardVs,
) {
    AppTheme {
        HorizontalCard(
            modifier = Modifier.fillMaxWidth(),
            viewState = vs,
            onClick = {},
        )
    }
}

@Preview(name = "Static icon")
@Composable
private fun HorizontalCardIconPreview() {
    val vs = HorizontalCardVs(
        id = "",
        imageUrl = "",
        title = "Horiz card title",
        description = UiText.Dynamic("Card • Description"),
    )

    AppTheme {
        HorizontalCard(
            modifier = Modifier.fillMaxWidth(),
            viewState = vs,
            trailingIcon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            onClick = {},
        )
    }
}

@AppPreview
@Composable
private fun HorizontalCardPlayingPreview() {
    val vs = HorizontalCardVs(
        id = "",
        imageUrl = "",
        title = "Horiz card title",
        description = UiText.Dynamic("Card • Description"),
    )

    AppTheme {
        HorizontalCard(
            modifier = Modifier.fillMaxWidth(),
            viewState = vs,
            trailingIcon = {
                PlayingIcon(
                    modifier = Modifier.matchParentSize(),
                    isPlaying = true,
                )
            },
            onClick = {},
        )
    }
}

private class HorizontalVsProvider : PreviewParameterProvider<HorizontalCardVs> {
    override val values: Sequence<HorizontalCardVs> = List(5) { i ->
        HorizontalCardVs(
            id = i.toString(),
            imageUrl = "".takeIf { i % 2 == 0 },
            title = "Horiz card title $i ".repeat(i + 1).trim(),
            description = UiText.Dynamic("Card_$i • Description ".repeat(i + 1).trim()),
        )
    }
        .asSequence()
}
