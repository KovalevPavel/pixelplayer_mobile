package kovp.pixelplayer.feature_albums.presentation

import kotlinx.collections.immutable.ImmutableList
import kovp.pixelplayer.core_ui.components.vertical_card.VerticalCardVs

internal sealed interface AlbumsState {
    data object Loading : AlbumsState

    data class List(
        val albums: ImmutableList<VerticalCardVs>,
    ) : AlbumsState

    data class Error(
        val message: String,
        val action: String,
    ) : AlbumsState
}
