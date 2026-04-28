package kov_p.pixelplayer.feature_artists.presentation

import kotlinx.collections.immutable.ImmutableList
import kov_p.pixelplayer.core_ui.UiText
import kov_p.pixelplayer.core_ui.components.vertical_card.VerticalCardVs

internal sealed interface ArtistsState {
    data object Loading : ArtistsState

    data class List(
        val artists: ImmutableList<VerticalCardVs>,
    ) : ArtistsState

    data class Error(
        val message: UiText,
        val action: UiText,
    ) : ArtistsState
}
