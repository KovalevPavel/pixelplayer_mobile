package kovp.pixelplayer.feature_artists.presentation

internal sealed interface ArtistsAction {
    data class OnArtistClick(val artistId: String) : ArtistsAction
    data object OnErrorActionClick : ArtistsAction
    data object FetchArtists : ArtistsAction
}
