package kovp.pixelplayer.core_player

interface Player {
    fun play(uri: String)
    fun pause()
    fun stop()
    fun next()
    fun previous()
    fun seekTo(position: Long)
}
