package kovp.pixelplayer.core_player.di

import kovp.pixelplayer.core.context.AppContext
import org.koin.core.module.Module

internal expect fun Module.bindPlayer(ctx: AppContext, token: String, baseUrl: String)
