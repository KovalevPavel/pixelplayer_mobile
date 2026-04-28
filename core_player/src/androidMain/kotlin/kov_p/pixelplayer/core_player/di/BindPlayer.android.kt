package kov_p.pixelplayer.core_player.di

import android.content.ComponentName
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.SessionToken
import kov_p.pixelplayer.core_player.AndroidPlayer
import kov_p.pixelplayer.core_player.PlaybackService
import kov_p.pixelplayer.core_player.TokenProvider
import kov_p.pixelplayer.core.context.AndroidAppContext
import kov_p.pixelplayer.core.context.AppContext
import org.koin.core.module.dsl.onClose
import org.koin.core.module.dsl.withOptions
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
        AndroidPlayer(
            context = ctx,
            sessionToken = get(),
            baseUrl = baseUrl,
        )
    }
        .withOptions {
            onClose { player -> player?.release() }
        }
        .bind<kov_p.pixelplayer.core_player.Player>()
}
