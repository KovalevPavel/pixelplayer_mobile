package kovp.pixelplayer.feature_tracks.presentation

import kotlinx.collections.immutable.ImmutableList
import kovp.pixelplayer.core_ui.components.vertical_card.VerticalCardVs

internal sealed interface TracksState {
    data object Loading : TracksState

    data class List(
        val tracks: ImmutableList<VerticalCardVs>,
    ) : TracksState

    data class Error(
        val message: String,
        val action: String,
    ) : TracksState
}
