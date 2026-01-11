package kovp.pixelplayer.domain_albums

data class AlbumVo(
    val id: String,
    val title: String,
    val cover: String,
    val artist: String,
    val year: String,
    val tracks: List<TrackVo> = emptyList(),
) {
    data class TrackVo(
        val id: String,
        val title: String,
        val position: Int,
    )
}
