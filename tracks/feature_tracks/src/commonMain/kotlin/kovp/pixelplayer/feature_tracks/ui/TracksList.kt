package kovp.pixelplayer.feature_tracks.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_player.PlayerViewModel
import kovp.pixelplayer.core_ui.components.horizontal_card.HorizontalCard
import kovp.pixelplayer.core_ui.components.horizontal_card.HorizontalCardVs
import kovp.pixelplayer.core_ui.components.playing_icon.PlayingIcon
import kovp.pixelplayer.feature_tracks.presentation.TracksAction
import kovp.pixelplayer.feature_tracks.presentation.TracksState
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.getKoin

@Composable
internal fun TracksList(
    state: TracksState.List,
    handleAction: (TracksAction) -> Unit,
) {
    val koin = getKoin()
    val playerVm: PlayerViewModel = remember { koin.get() }
    val playingTrackId by playerVm.currentPlaying.collectAsState()

    val timeline by playerVm.timeline.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnimatedVisibility(
            visible = timeline != null && playingTrackId != null
        ) {
            TimeLine(
                track = playingTrackId ?: return@AnimatedVisibility,
                fraction = timeline?.fraction ?: return@AnimatedVisibility
            )
        }

        TrackList(
            tracks = state.tracks,
            currentPlaying = playingTrackId,
            onClick = {
                if (playingTrackId == it) {
                    playerVm.pause()
                } else {
                    playerVm.play(it)
                }
            },
        )
    }
}

@Composable
private fun TimeLine(
    track: String,
    fraction: Float,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = track,
        )

        Box(modifier = Modifier
            .background(Color(0xffff8000))
            .height(8.dp)
            .fillMaxWidth(fraction))
    }
}

@Composable
private fun ColumnScope.TrackList(
    tracks: ImmutableList<HorizontalCardVs>,
    currentPlaying: String?,
    onClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().weight(1f),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
    ) {
        items(items = tracks, key = HorizontalCardVs::id) { item ->
            HorizontalCard(
                viewState = item,
                onClick = { onClick(item.id) },
                trailingIcon = {
                    if (item.id == currentPlaying) {
                        PlayingIcon()
                    } else {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            )
        }
    }
}

@AppPreview
@Composable
private fun TracksListPreview(
    @PreviewParameter(TracksListStateProvider::class) state: TracksState.List,
) {
    AppTheme {
        TracksList(state = state, handleAction = {})
    }
}

private class TracksListStateProvider : PreviewParameterProvider<TracksState.List> {
    override val values: Sequence<TracksState.List> = List(2) { i ->
        TracksState.List(
            tracks = List((i + 1) * 4) {
                HorizontalCardVs(
                    id = it.toString(),
                    title = "Track $it ".repeat(it + 1),
                    imageUrl = "",
                    description = "Albums: $it".repeat(10 * (it)),
                )
            }
                .toImmutableList()
        )
    }
        .asSequence()
}
