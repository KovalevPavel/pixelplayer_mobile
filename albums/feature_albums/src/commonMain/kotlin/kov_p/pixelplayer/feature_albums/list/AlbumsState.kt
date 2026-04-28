package kov_p.pixelplayer.feature_albums.list

import kotlinx.collections.immutable.ImmutableList
import kov_p.pixelplayer.core_ui.UiText
import kov_p.pixelplayer.core_ui.components.vertical_card.VerticalCardVs

internal sealed interface AlbumsState {
    data object Loading : AlbumsState

    data class List(
        val albums: ImmutableList<VerticalCardVs>,
    ) : AlbumsState

    data class Error(
        val message: UiText,
        val action: UiText,
    ) : AlbumsState
}
