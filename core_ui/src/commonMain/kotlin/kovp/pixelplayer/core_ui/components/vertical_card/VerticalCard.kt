package kovp.pixelplayer.core_ui.components.vertical_card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.pixelTypography
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_ui.UiText
import kovp.pixelplayer.core_ui.asString
import kovp.pixelplayer.core_ui.components.image.PixelImage
import kovp.pixelplayer.core_ui.withAnimation
import kotlin.math.pow

@Composable
fun VerticalCard(
    viewState: VerticalCardVs,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
        onClick = onClick,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PixelImage(
                modifier = Modifier
                    .withAnimation(key = viewState.imageUrl)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
                url = viewState.imageUrl,
            )

            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth()
                        .withAnimation("${viewState.id}_${viewState.title}"),
                    text = viewState.title.asString(),
                    style = pixelTypography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    modifier = Modifier.fillMaxWidth()
                        .withAnimation("${viewState.id}_${viewState.description}"),
                    text = viewState.description.asString(),
                    style = pixelTypography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                viewState.tagline?.let { tag ->
                    Text(
                        modifier = Modifier.fillMaxWidth()
                            .withAnimation("${viewState.id}_${viewState.tagline}"),
                        text = tag.asString(),
                        style = pixelTypography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@AppPreview
@Composable
private fun VerticalCardPreview(
    @PreviewParameter(ArtistProvider::class) vs: VerticalCardVs,
) {
    AppTheme {
        Box(modifier = Modifier.width(200.dp)) {
            VerticalCard(
                viewState = vs,
                onClick = {},
            )
        }
    }
}

private class ArtistProvider : PreviewParameterProvider<VerticalCardVs> {
    override val values: Sequence<VerticalCardVs> = List(4) { index ->
        VerticalCardVs(
            id = "",
            title = UiText.Dynamic("Artist name ".repeat(index + 1).trim()),
            imageUrl = "",
            description = UiText.Dynamic("Albums: ${20.0.pow(index).toInt()}"),
            tagline = UiText.Dynamic(if (index % 2 == 0) "2007" else "")
                .takeIf { index % 3 == 0 },
        )
    }
        .asSequence()
}
