package kovp.pixelplayer.core_player

import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kovp.pixelplayer.core.context.AndroidAppContext
import kotlin.math.roundToLong

private typealias PlayerImpl = androidx.media3.common.Player
private const val PROGRESS_UPDATE_INTERVAL_MS = 250L

internal class AndroidPlayer(
    private val context: AndroidAppContext,
    private val baseUrl: String,
    sessionToken: SessionToken,
) : Player {
    override val playerVs: StateFlow<PlayerVs> by lazy { _playerState }
    private val _playerState = MutableStateFlow<PlayerVs>(PlayerVs.Empty)
    private val playerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var progressUpdateJob: Job? = null
    private var isReleased = false

    private val listener = object : androidx.media3.common.Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            refreshStateAndSyncProgressLoop()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            refreshStateAndSyncProgressLoop()
        }

        override fun onPositionDiscontinuity(
            oldPosition: androidx.media3.common.Player.PositionInfo,
            newPosition: androidx.media3.common.Player.PositionInfo,
            reason: Int,
        ) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            refreshStateAndSyncProgressLoop()
        }

        override fun onTimelineChanged(timeline: androidx.media3.common.Timeline, reason: Int) {
            super.onTimelineChanged(timeline, reason)
            refreshStateAndSyncProgressLoop()
        }
    }

    private val controllerFuture = MediaController.Builder(context.context, sessionToken)
        .setListener(
            object : MediaController.Listener {
                override fun onDisconnected(controller: MediaController) {
                    shutdownPlayerState()
                    controller.removeListener(listener)
                    super.onDisconnected(controller)
                }
            }
        )
        .buildAsync()
        .also { future ->
            future.addListener(
                {
                    if (isReleased) {
                        MediaController.releaseFuture(future)
                        return@addListener
                    }

                    runCatching { future.get() }
                        .getOrNull()
                        ?.let { controller ->
                            controller.addListener(listener)
                            refreshStateAndSyncProgressLoop()
                        }
                },
                ContextCompat.getMainExecutor(context.context),
            )
        }

    private val controller: MediaController by lazy {
        controllerFuture.get()
    }

    override fun loadTracks(tracks: List<TrackIn>, clear: Boolean) {
        doIfAvailable(
            PlayerImpl.COMMAND_SET_MEDIA_ITEM,
            PlayerImpl.COMMAND_PREPARE,
            PlayerImpl.COMMAND_CHANGE_MEDIA_ITEMS,
        ) {
            if (clear) {
                clearMediaItems()
            }

            tracks.map { track ->
                mapMediaItem(trackId = track.trackId, metadata = track.metadata)
            }
                .let(::setMediaItems)
            prepare()
        }
    }

    override fun play(
        id: String,
        metadata: TrackIn.TrackMetaData?,
    ) {
        doIfAvailable(
            PlayerImpl.COMMAND_SET_MEDIA_ITEM,
            PlayerImpl.COMMAND_PREPARE,
            PlayerImpl.COMMAND_PLAY_PAUSE,
        ) {
            mapMediaItem(trackId = id, metadata = metadata)
                .let(::setMediaItem)

            prepare()
            play()
        }
    }

    override fun play(index: Int) {
        doIfAvailable(
            PlayerImpl.COMMAND_PLAY_PAUSE,
            PlayerImpl.COMMAND_SEEK_TO_MEDIA_ITEM,
        ) {
            seekTo(index, 0)
            play()
        }
    }

    override fun resume() {
        doIfAvailable(PlayerImpl.COMMAND_PLAY_PAUSE) {
            play()
        }
    }

    override fun pause() {
        doIfAvailable(PlayerImpl.COMMAND_PLAY_PAUSE) {
            pause()
            refreshStateAndSyncProgressLoop()
        }
    }

    override fun next() {
        doIfAvailable(PlayerImpl.COMMAND_SEEK_TO_NEXT) {
            seekToNext()
        }
    }

    override fun previous() {
        doIfAvailable(PlayerImpl.COMMAND_SEEK_TO_PREVIOUS) {
            seekToPrevious()
        }
    }

    override fun seekTo(fraction: Float) {
        doIfAvailable(PlayerImpl.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM) {
            val newPosition = (duration * fraction).roundToLong()
            seekTo(newPosition)
        }
    }

    override fun clearPlayer() {
        doIfAvailable(PlayerImpl.COMMAND_STOP) {
            stopProgressLoop()
            stop()
            clearMediaItems()
            clearPlayerState()
        }
    }

    internal fun release() {
        if (isReleased) {
            return
        }
        isReleased = true

        shutdownPlayerState()
        playerScope.coroutineContext.cancelChildren()

        ContextCompat.getMainExecutor(context.context).execute {
            if (controllerFuture.isDone) {
                runCatching { controllerFuture.get() }
                    .getOrNull()
                    ?.let { controller ->
                        controller.removeListener(listener)
                        controller.release()
                    }
            } else {
                MediaController.releaseFuture(controllerFuture)
            }
        }
    }

    private fun refreshStateAndSyncProgressLoop() {
        if (isReleased) {
            return
        }
        updatePlayerState()
        syncProgressLoopWithPlayback()
    }

    private fun syncProgressLoopWithPlayback() {
        if (controller.isPlaying && isActiveForUpdates()) {
            startProgressLoop()
        } else {
            stopProgressLoop()
        }
    }

    private fun startProgressLoop() {
        if (isReleased) {
            return
        }
        progressUpdateJob?.cancel()
        progressUpdateJob = playerScope.launch {
            while (isActive && controller.isPlaying && isActiveForUpdates()) {
                updatePlayerState()
                delay(PROGRESS_UPDATE_INTERVAL_MS)
            }
        }
    }

    private fun stopProgressLoop() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }

    private fun shutdownPlayerState() {
        stopProgressLoop()
        clearPlayerState()
    }

    private fun clearPlayerState() {
        _playerState.value = PlayerVs.Empty
    }

    private fun updatePlayerState() {
        if (isReleased) {
            return
        }
        _playerState.update {
            val currentMediaItem = controller.currentMediaItem ?: return@update PlayerVs.Empty
            val metadata = currentMediaItem.mediaMetadata
            PlayerVs.Data(
                trackId = currentMediaItem.mediaId,
                metaData = TrackIn.TrackMetaData(
                    trackTitle = metadata.title?.toString(),
                    album = metadata.albumTitle?.toString(),
                    artist = metadata.artist?.toString(),
                    disk = metadata.discNumber,
                    position = metadata.trackNumber,
                ),
                timeLine = PlayerVs.AudioTimeline(
                    isPlaying = controller.isPlaying,
                    currentPositionMs = controller.currentPosition,
                    durationMs = controller.duration,
                ),
                hasNext = controller.hasNextMediaItem(),
            )
        }
    }

    private fun isActiveForUpdates(): Boolean {
        return !isReleased && controller.currentMediaItem != null
    }

    private fun doIfAvailable(vararg command: Int, action: MediaController.() -> Unit) {
        if (isReleased) {
            return
        }
        if (command.all(controller::isCommandAvailable)) {
            controller.action()
        }
    }

    private fun mapMediaItem(
        trackId: String,
        metadata: TrackIn.TrackMetaData?,
    ): MediaItem {
        return MediaItem.Builder()
            .setMediaId(trackId)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(metadata?.trackTitle)
                    .setAlbumTitle(metadata?.album)
                    .setArtist(metadata?.artist)
                    .setDiscNumber(metadata?.disk)
                    .setTrackNumber(metadata?.position)
                    .build()
            )
            .setUri(mapUrl(trackId))
            .build()
    }

    private fun mapUrl(uri: String): String {
        return "$baseUrl/api/play/$uri"
    }
}
