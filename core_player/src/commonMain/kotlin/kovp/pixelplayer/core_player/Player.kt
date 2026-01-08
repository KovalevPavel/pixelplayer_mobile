package kovp.pixelplayer.core_player

import kotlinx.coroutines.flow.StateFlow

interface Player {
    val currentPlaying: StateFlow<String?>
    val currentTimeLineFlow: StateFlow<AudioTimeline?>

    fun play(id: String)
    fun pause()
    fun stop()
    fun next()
    fun previous()
    fun seekTo(position: Long)
}
