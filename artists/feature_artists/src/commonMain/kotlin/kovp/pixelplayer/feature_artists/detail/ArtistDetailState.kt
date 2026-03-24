package kovp.pixelplayer.feature_artists.detail

import kotlinx.collections.immutable.ImmutableList
import kovp.pixelplayer.core_ui.components.vertical_card.VerticalCardVs

sealed interface ArtistDetailState {
    data class Error(val message: String) : ArtistDetailState

    sealed interface Content : ArtistDetailState

    data object Loading : Content

    data class Data(
        val artistName: String,
        val avatar: String,
        val albums: ImmutableList<VerticalCardVs>,
    ) : Content
}
