package kovp.pixelplayer.core_player

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class AndroidPlayer(
    private val controller: AndroidAudioController,
) : Player {
    override val currentPlaying: StateFlow<String?> by lazy { _currentPlaying }
    override val currentTimeLineFlow: StateFlow<AudioTimeline?> by lazy { _currentTimeLineFlow }

    private val _currentPlaying = MutableStateFlow<String?>(null)
    private val _currentTimeLineFlow = MutableStateFlow<AudioTimeline?>(null)

    private val scope = CoroutineScope(Dispatchers.Default)

    private val listener by lazy {
        object : androidx.media3.common.Player.Listener {
            private var timelineJob: Job? = null
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                timelineJob?.cancel()
                if (isPlaying) {
                    timelineJob = CoroutineScope(Dispatchers.Main).launch {
                        while (true) {
                            _currentTimeLineFlow.emit(
                                AudioTimeline(
                                    currentPositionMs = controller.currentPosition,
                                    durationMs = controller.duration,
                                )
                            )
                            delay(500)
                        }
                    }
                }
            }
        }
    }

    init {
        controller.isInitialized
            .onEach { isInitialized ->
                if (isInitialized) {
                    controller.addListener(listener)
                }
            }
            .launchIn(scope)
    }

    override fun play(id: String) {
        _currentPlaying.value = id
        controller.play(uri = id)
    }

    override fun pause() {
        _currentPlaying.value = null
        controller.pause()
    }

    override fun stop() {
        controller.stop()
    }

    override fun next() {
        controller.next()
    }

    override fun previous() {
        controller.prev()
    }

    override fun seekTo(position: Long) {
        controller.seekTo(position)
    }
}
