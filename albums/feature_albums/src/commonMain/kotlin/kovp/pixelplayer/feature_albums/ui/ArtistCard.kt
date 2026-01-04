package kovp.pixelplayer.feature_albums.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_design.AppTypography
import kovp.pixelplayer.core_ui.components.image.PixelImage
import kovp.pixelplayer.feature_albums.presentation.AlbumVs
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import kotlin.math.pow

@Composable
internal fun ArtistCard(
    vs: AlbumVs,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
        onClick = onClick,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PixelImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                url = vs.cover,
            )

            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = vs.title,
                    style = AppTypography.titleMedium,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = vs.year,
                    style = AppTypography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(widthDp = 200)
@Composable
private fun ArtistCardPreview(
    @PreviewParameter(ArtistProvider::class) vs: AlbumVs,
) {
    AppTheme {
        ArtistCard(
            vs = vs,
            onClick = {},
        )
    }
}

private class ArtistProvider : PreviewParameterProvider<AlbumVs> {
    override val values: Sequence<AlbumVs> = List(4) {
        AlbumVs(
            id = "",
            title = "Artist name ".repeat(it + 1).trim(),
            cover = "",
            year = "Albums: ${20.0.pow(it).toInt()}",
        )
    }
        .asSequence()
}
