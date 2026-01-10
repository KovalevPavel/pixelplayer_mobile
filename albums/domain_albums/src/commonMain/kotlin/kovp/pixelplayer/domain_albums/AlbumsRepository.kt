package kovp.pixelplayer.domain_albums

interface AlbumsRepository {
    suspend fun getAllAlbums(): List<AlbumVo>
    suspend fun getAlbum(albumId: String): AlbumVo
}
