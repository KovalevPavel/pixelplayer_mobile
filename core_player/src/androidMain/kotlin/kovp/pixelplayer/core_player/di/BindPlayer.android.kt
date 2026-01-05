package kovp.pixelplayer.core_player.di

import android.content.ComponentName
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.SessionToken
import kovp.pixelplayer.core_player.AndroidAudioController
import kovp.pixelplayer.core_player.AndroidPlayer
import kovp.pixelplayer.core_player.PlaybackService
import kovp.pixelplayer.core_player.TokenProvider
import kovp.pixelplayer.core_player.context.AndroidAppContext
import kovp.pixelplayer.core_player.context.AppContext
import org.koin.core.module.Module
import org.koin.dsl.bind

@OptIn(UnstableApi::class)
internal actual fun Module.bindPlayer(ctx: AppContext, token: String, baseUrl: String) {
    if (ctx !is AndroidAppContext) {
        return
    }

    single { TokenProvider { token } }

    single {
        SessionToken(
            ctx.context,
            ComponentName(ctx.context, PlaybackService::class.java),
        )
    }

    single {
        AndroidAudioController(
            context = ctx,
            sessionToken = get(),
            baseUrl = baseUrl,
        )
    }

    single {
        AndroidPlayer(
            controller = get(),
        )
    }
        .bind<kovp.pixelplayer.core_player.Player>()
}
