package kov_p.pixelplayer.core_player.di

import kov_p.pixelplayer.core.context.AppContext
import org.koin.core.module.Module

internal actual fun Module.bindPlayer(
    ctx: AppContext,
    token: String,
    baseUrl: String,
) {
}