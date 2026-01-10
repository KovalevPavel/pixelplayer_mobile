package kovp.pixelplayer.feature_albums.detail

sealed interface AlbumDetailAction {
    data object FetchTracks : AlbumDetailAction
    data class OnTrackClick(val index: Int) : AlbumDetailAction
}
