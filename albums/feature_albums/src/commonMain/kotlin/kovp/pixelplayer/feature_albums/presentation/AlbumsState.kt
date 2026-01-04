package kovp.pixelplayer.feature_albums.presentation

import kotlinx.collections.immutable.ImmutableList

internal sealed interface AlbumsState {
    data object Loading : AlbumsState

    data class List(
        val artists: ImmutableList<AlbumVs>,
    ) : AlbumsState

    data class Error(
        val message: String,
        val action: String,
    ) : AlbumsState
}
