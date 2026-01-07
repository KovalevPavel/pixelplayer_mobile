package kovp.pixelplayer.core_player.di

import kovp.pixelplayer.core.context.AppContext
import kovp.pixelplayer.core_player.PlayerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun playerModule(ctx: AppContext, token: String, baseUrl: String) = module {
    bindPlayer(ctx, token, baseUrl)
    viewModelOf(::PlayerViewModel)
}
