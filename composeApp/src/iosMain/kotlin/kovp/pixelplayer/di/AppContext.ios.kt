package kovp.pixelplayer.di

import kovp.pixelplayer.AppContext
import org.koin.core.KoinApplication

class IosAppContext : AppContext

actual fun KoinApplication.bindContext(ctx: AppContext) {}
