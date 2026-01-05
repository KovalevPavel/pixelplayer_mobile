package kovp.pixelplayer.core_player.context

import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication

actual fun KoinApplication.bindContext(ctx: AppContext) {
    (ctx as? AndroidAppContext)?.context?.let(::androidContext)
}
