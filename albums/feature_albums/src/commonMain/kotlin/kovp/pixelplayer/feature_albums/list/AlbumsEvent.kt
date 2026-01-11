package kovp.pixelplayer.feature_albums.list

internal sealed interface AlbumsEvent {
    data class NavigateToAlbum(val albumId: String) : AlbumsEvent
}
