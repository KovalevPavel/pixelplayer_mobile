package kovp.pixelplayer.core_player

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class PlayerViewModel internal constructor(
    private val player: Player,
) : ViewModel() {
    val currentPlaying: StateFlow<String?> = player.currentPlaying
    val timeline: StateFlow<AudioTimeline?> = player.currentTimeLineFlow

    private var lastCached: String? = null
    private var cachedPos: Long? = null

    fun play(id: String) {
        if (lastCached == id && cachedPos != null) {
            player.seekTo(cachedPos ?: 0)
        }
        lastCached = null
        cachedPos = null
        player.play(id)
    }

    fun pause() {
        lastCached = currentPlaying.value
        player.pause()
        cachedPos = timeline.value?.currentPositionMs
    }
}
