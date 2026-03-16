package kovp.pixelplayer.domain_albums

import kotlin.time.Duration

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
        val disk: Int,
        val duration: Duration,
        val quality: Quality,
    )

    sealed interface Quality {
        data object Lossless : Quality

        data class Bitrate(val bitrate: Int) : Quality
    }
}
