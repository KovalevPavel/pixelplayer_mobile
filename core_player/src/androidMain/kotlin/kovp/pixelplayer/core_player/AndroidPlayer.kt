package kovp.pixelplayer.core_player

internal class AndroidPlayer(
    private val controller: AndroidAudioController,
) : Player {
    override fun play(uri: String) {
        controller.play(uri = uri)
    }

    override fun pause() {
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
