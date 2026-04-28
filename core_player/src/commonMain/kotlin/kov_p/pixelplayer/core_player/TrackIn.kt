package kov_p.pixelplayer.core_player

data class TrackIn(
    val trackId: String,
    val metadata: TrackMetaData?,
) {
    data class TrackMetaData(
        val trackTitle: String?,
        val album: String?,
        val albumId: String?,
        val artist: String?,
        val disk: Int?,
        val position: Int?,
    ) {
        companion object {
            val stub = TrackMetaData(
                trackTitle = null,
                album = null,
                albumId = null,
                artist = null,
                disk = null,
                position = null,
            )
        }
    }
}
