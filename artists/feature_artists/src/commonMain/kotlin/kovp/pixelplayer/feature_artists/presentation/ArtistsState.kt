package kovp.pixelplayer.feature_artists.presentation

import kotlinx.collections.immutable.ImmutableList
import kovp.pixelplayer.core_ui.components.vertical_card.VerticalCardVs

internal sealed interface ArtistsState {
    data object Loading : ArtistsState

    data class List(
        val artists: ImmutableList<VerticalCardVs>,
    ) : ArtistsState

    data class Error(
        val message: String,
        val action: String,
    ) : ArtistsState
}
