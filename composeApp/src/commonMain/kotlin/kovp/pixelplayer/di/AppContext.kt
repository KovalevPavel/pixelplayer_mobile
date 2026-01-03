package kovp.pixelplayer.di

import kovp.pixelplayer.AppContext
import org.koin.core.KoinApplication

expect fun KoinApplication.bindContext(ctx: AppContext)
