package kovp.pixelplayer.core_player

data class TrackIn(
    val trackId: String,
    val metadata: TrackMetaData?,
) {
    data class TrackMetaData(
        val trackTitle: String? = null,
        val album: String? = null,
        val artist: String? = null,
    )
}
