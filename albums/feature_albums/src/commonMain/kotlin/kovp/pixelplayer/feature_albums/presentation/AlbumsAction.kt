package kovp.pixelplayer.feature_albums.presentation

internal sealed interface AlbumsAction {
    data class OnAlbumClick(val albumId: String) : AlbumsAction
    data object OnErrorActionClick : AlbumsAction
    data object FetchAlbums : AlbumsAction
}
