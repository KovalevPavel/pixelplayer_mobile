package kovp.pixelplayer.androidApp.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import kov_p.pixelplayer.network.di.okHttpInterceptor
import okhttp3.Interceptor
import org.koin.dsl.module

val androidAppModule = module {
    single<Interceptor>(qualifier = okHttpInterceptor) {
        ChuckerInterceptor.Builder(get<Context>()).build()
    }
}
