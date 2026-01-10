package kovp.pixelplayer.core_player

import kotlinx.coroutines.flow.StateFlow

interface Player {
    val playerVs: StateFlow<PlayerVs>

    fun loadTracks(vararg track: TrackIn, clear: Boolean = true)

    fun loadTracks(tracks: List<TrackIn>, clear: Boolean = true)

    fun play(
        id: String,
        metadata: TrackIn.TrackMetaData?,
    )

    fun play(index: Int)

    fun resume()
    fun pause()
    fun next()
    fun previous()
    fun seekTo(fraction: Float)
}
