package kovp.pixelplayer.feature_albums.presentation

internal sealed interface AlbumsEvent {
    data class NavigateToAlbum(val albumId: String) : AlbumsEvent
}
