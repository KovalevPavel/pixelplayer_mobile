package kovp.pixelplayer.feature_artists.presentation

import kotlinx.collections.immutable.ImmutableList

internal sealed interface ArtistsState {
    data object Loading : ArtistsState

    data class List(
        val artists: ImmutableList<ArtistVs>,
    ) : ArtistsState

    data class Error(
        val message: String,
        val action: String,
    ) : ArtistsState
}
