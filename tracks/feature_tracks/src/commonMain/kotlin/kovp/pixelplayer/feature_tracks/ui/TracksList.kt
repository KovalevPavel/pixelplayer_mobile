package kovp.pixelplayer.feature_tracks.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableList
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_ui.components.horizontal_card.HorizontalCard
import kovp.pixelplayer.core_ui.components.horizontal_card.HorizontalCardVs
import kovp.pixelplayer.feature_tracks.presentation.TracksAction
import kovp.pixelplayer.feature_tracks.presentation.TracksState
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

@Composable
internal fun TracksList(
    state: TracksState.List,
    handleAction: (TracksAction) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
    ) {
        items(items = state.tracks, key = HorizontalCardVs::id) { item ->
            HorizontalCard(
                viewState = item,
                onClick = {},
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
