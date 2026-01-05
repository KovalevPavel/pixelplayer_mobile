package kovp.pixelplayer.core_player

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal fun interface TokenProvider {
    fun provide(): String
}

class PlaybackService : MediaSessionService(), KoinComponent {
    private var mediaSession: MediaSession? = null
    private var player: ExoPlayer? = null
    private val tokenProvider: TokenProvider by inject()

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo,
    ): MediaSession? {
        return mediaSession
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        val dataSourceFactory =
            DataSource.Factory {
                httpDataSourceFactory
                    .setAllowCrossProtocolRedirects(true)
                    .setDefaultRequestProperties(
                        mapOf(
                            "Authorization" to tokenProvider.provide(),
                        ),
                    )
                    .createDataSource()
            }

        player = ExoPlayer.Builder(this)
            .setHandleAudioBecomingNoisy(true)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(this).setDataSourceFactory(dataSourceFactory),
            )
            .build()

        mediaSession = MediaSession.Builder(this, player ?: return)
            .build()
    }

    override fun onDestroy() {
        mediaSession?.release()
        player?.release()

        super.onDestroy()
    }
}
