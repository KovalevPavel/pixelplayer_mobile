package kovp.pixelplayer.feature_artists.detail

sealed interface ArtistDetailEvent {
    data class NavigateToAlbum(val albumId: String) : ArtistDetailEvent
}
