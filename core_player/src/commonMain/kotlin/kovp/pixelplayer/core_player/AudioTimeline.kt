package kovp.pixelplayer.core_player

import kotlin.time.Duration.Companion.milliseconds

@Suppress("unused")
data class AudioTimeline(
    val currentPositionMs: Long,
    private val durationMs: Long,
) {
    val fraction: Float
        get() = currentPositionMs.toFloat() / durationMs

    val formattedPosition: String
        get() = format(currentPositionMs)

    val formattedDuration: String
        get() = format(durationMs)

    private fun format(ms: Long): String {
        val durationSeconds = ms.milliseconds.inWholeSeconds
        val durationMinutes = durationSeconds / 60
        val remainSeconds = durationSeconds % 60

        val minutes = durationMinutes.toString().padStart(2, '0')
        val seconds = remainSeconds.toString().padStart(2, '0')
        return "$minutes:$seconds"
    }
}
