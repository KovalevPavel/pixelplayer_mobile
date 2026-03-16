package kovp.pixelplayer.feature_albums.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_design.pixelColors
import kovp.pixelplayer.core_design.pixelTypography
import kovp.pixelplayer.core_ui.components.playing_icon.PlayingIcon
import kovp.pixelplayer.domain_albums.AlbumVo

@Composable
fun TrackCardComposable(
    modifier: Modifier = Modifier,
    viewState: AlbumDetailState.TrackVs,
    currentPlaying: String,
    isPlaying: Boolean,
    onTrackClick: () -> Unit,
) {
    val measurer = rememberTextMeasurer()

    val results = measurer.measure(
        text = "000",
        style = pixelTypography.bodyLarge,
    )

    Row(
        modifier = modifier.clickable(onClick = onTrackClick).padding(all = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val widthDp = LocalDensity.current.run { results.size.width.toDp() }

        Text(
            modifier = Modifier.width(widthDp),
            text = viewState.position.toString(),
            style = pixelTypography.titleLarge,
            color = pixelColors.onSurface,
            textAlign = TextAlign.Center,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = viewState.title,
                style = pixelTypography.bodyLarge,
                color = pixelColors.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = viewState.artist,
                style = pixelTypography.bodyMedium,
                color = pixelColors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (currentPlaying == viewState.id) {
            PlayingIcon(isPlaying = isPlaying)
        } else {
            Text(
                text = viewState.duration,
                style = pixelTypography.bodyMedium,
                color = pixelColors.onSurfaceVariant,
            )
        }
    }
}

@AppPreview
@Composable
private fun TrackCardPreview(
    @PreviewParameter(TrackVsProvider::class) viewState: AlbumDetailState.TrackVs,
) {
    AppTheme {
        TrackCardComposable(
            viewState = viewState,
            currentPlaying = "1",
            isPlaying = true,
            onTrackClick = {},
        )
    }
}

private class TrackVsProvider : PreviewParameterProvider<AlbumDetailState.TrackVs> {
    override val values: Sequence<AlbumDetailState.TrackVs> = sequenceOf(
        AlbumDetailState.TrackVs(
            id = "0",
            title = "Track title",
            artist = "Artist",
            position = 2,
            globalPosition = 2,
            duration = "3:53",
            quality = AlbumVo.Quality.Lossless,
        ),
        AlbumDetailState.TrackVs(
            id = "1",
            title = "Track title",
            artist = "Artist",
            position = 2,
            globalPosition = 2,
            duration = "3:53",
            quality = AlbumVo.Quality.Bitrate(320),
        ),
    )
}
