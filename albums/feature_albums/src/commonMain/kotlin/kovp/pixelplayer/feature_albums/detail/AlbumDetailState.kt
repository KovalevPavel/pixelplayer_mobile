package kovp.pixelplayer.feature_albums.detail

import kotlinx.collections.immutable.ImmutableList
import kovp.pixelplayer.core_ui.components.horizontal_card.HorizontalCardVs

sealed interface AlbumDetailState {
    data object Loading : AlbumDetailState
    data class Data(
        val title: String,
        val artist: String,
        val cover: String,
        val year: String,
        val tracks: ImmutableList<HorizontalCardVs>,
    ) : AlbumDetailState
}
