package kovp.pixelplayer.feature_albums.list.ui

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
import kovp.pixelplayer.feature_albums.list.AlbumsAction
import kovp.pixelplayer.feature_albums.list.AlbumsState
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

@Composable
internal fun AlbumsList(
    state: AlbumsState.List,
    handleAction: (AlbumsAction) -> Unit,
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 32.dp),
    ) {
        items(items = state.albums, key = VerticalCardVs::id) { item ->
            VerticalCard(
                viewState = item,
                onClick = {
                    handleAction(AlbumsAction.OnAlbumClick(item.id))
                },
            )
        }
    }
}

@AppPreview
@Composable
private fun AlbumsListPreview(
    @PreviewParameter(ArtistsListProvider::class) state: AlbumsState.List,
) {
    AppTheme {
        AlbumsList(state = state, handleAction = {})
    }
}

private class ArtistsListProvider : PreviewParameterProvider<AlbumsState.List> {
    override val values: Sequence<AlbumsState.List> = List(2) { i ->
        AlbumsState.List(
            albums = List((i + 1) * 3) {
                VerticalCardVs(
                    id = it.toString(),
                    title = "Album $it ".repeat(it + 1),
                    imageUrl = "",
                    description = "2007",
                )
            }
                .toImmutableList()
        )
    }
        .asSequence()
}
