package kovp.pixelplayer.feature_tracks.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_player.PlayerViewModel
import kovp.pixelplayer.core_player.data.TrackMetaData
import kovp.pixelplayer.core_ui.components.horizontal_card.HorizontalCard
import kovp.pixelplayer.core_ui.components.horizontal_card.HorizontalCardVs
import kovp.pixelplayer.core_ui.components.player.PlayerVs
import kovp.pixelplayer.core_ui.components.playing_icon.PlayingIcon
import kovp.pixelplayer.feature_tracks.presentation.TracksState
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.getKoin

@Composable
internal fun TracksList(
    state: TracksState.List,
) {
    val koin = getKoin()
    val playerVm: PlayerViewModel = remember { koin.get() }
    val playerVs by playerVm.playerVs.collectAsState()

    TrackListData(
        tracks = state.tracks,
        currentPlaying = playerVs.trackId,
        onClick = { id, payload ->
            // TODO: tune this statement
            when {
                (playerVs as? PlayerVs.Data)?.trackId != id -> {
                    playerVm.play(
                        id = id,
                        metaData = payload as? TrackMetaData,
                    )
                }

                else -> {
                    playerVm.pause()
                }
            }
        },
    )
}

@Composable
private fun TrackListData(
    tracks: ImmutableList<HorizontalCardVs>,
    currentPlaying: String?,
    onClick: (id: String, payload: Any?) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 32.dp),
    ) {
        items(items = tracks, key = HorizontalCardVs::id) { item ->
            HorizontalCard(
                viewState = item,
                onClick = { onClick(item.id, item.payload) },
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
        TrackListData(
            tracks = state.tracks,
            currentPlaying = "1",
            onClick = { _, _ -> },
        )
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
