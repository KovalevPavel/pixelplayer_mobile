package kovp.pixelplayer.feature_artists.detail

sealed interface ArtistDetailAction {
    data object FetchData : ArtistDetailAction
    data class OnAlbumClick(val albumId: String) : ArtistDetailAction
}
