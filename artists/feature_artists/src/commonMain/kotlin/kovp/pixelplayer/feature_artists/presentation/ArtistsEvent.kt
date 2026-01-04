package kovp.pixelplayer.feature_artists.presentation

internal sealed interface ArtistsEvent {
    data class NavigateToArtist(val artistId: String) : ArtistsEvent
}
