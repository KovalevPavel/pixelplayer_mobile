package kovp.pixelplayer.di

import android.content.Context
import kovp.pixelplayer.AppContext
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication

class AndroidAppContext(
    val context: Context,
) : AppContext

actual fun KoinApplication.bindContext(ctx: AppContext) {
    (ctx as AndroidAppContext).let {
        androidContext(it.context)
    }
}
