package kovp.pixelplayer.feature_artists.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableList
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_ui.components.vertical_card.VerticalCard
import kovp.pixelplayer.core_ui.components.vertical_card.VerticalCardVs
import kovp.pixelplayer.feature_artists.presentation.ArtistsAction
import kovp.pixelplayer.feature_artists.presentation.ArtistsState
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

@Composable
internal fun ArtistsList(
    state: ArtistsState.List,
    handleAction: (ArtistsAction) -> Unit,
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(items = state.artists, key = VerticalCardVs::id) { item ->
            VerticalCard(
                viewState = item,
                onClick = {
                    handleAction(ArtistsAction.OnArtistClick(artistId = item.id))
                },
            )
        }
    }
}

@AppPreview
@Composable
private fun ArtistsListPreview(
    @PreviewParameter(ArtistsListProvider::class) state: ArtistsState.List,
) {
    AppTheme {
        ArtistsList(state = state, handleAction = {})
    }
}

private class ArtistsListProvider : PreviewParameterProvider<ArtistsState.List> {
    override val values: Sequence<ArtistsState.List> = List(2) { i ->
        ArtistsState.List(
            artists = List((i + 1) * 3) {
                VerticalCardVs(
                    id = it.toString(),
                    title = "Artist $it ".repeat(it + 1),
                    imageUrl = "",
                    description = "Albums: $it".repeat(10 * (it)),
                )
            }
                .toImmutableList()
        )
    }
        .asSequence()
}
