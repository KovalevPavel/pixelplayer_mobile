package kovp.pixelplayer.domain_artists

interface ArtistsRepository {
    suspend fun getAllArtists(): List<ArtistVo>
    suspend fun getArtistInfo(artistId: String): ArtistVo
}
