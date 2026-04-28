package kov_p.pixelplayer.domain_artists

data class ArtistVo(
    val id: String,
    val name: String,
    val avatar: String,
    val albums: List<ArtistAlbumVo>,
) {
    data class ArtistAlbumVo(
        val id: String,
        val title: String,
        val cover: String,
        val year: String,
        val tracks: Int,
    )
}
