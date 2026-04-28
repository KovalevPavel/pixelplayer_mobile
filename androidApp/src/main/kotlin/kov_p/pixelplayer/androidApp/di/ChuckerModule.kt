package kov_p.pixelplayer.androidApp.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import kov_p.pixelplayer.network.di.demoServerInterceptor
import kov_p.pixelplayer.network.di.okHttpInterceptor
import kov_p.pixelplayer.androidApp.di.DemoServerInterceptor
import okhttp3.Interceptor
import org.koin.dsl.module

val androidAppModule = module {
    single<Interceptor>(qualifier = okHttpInterceptor) {
        ChuckerInterceptor.Builder(context = get<Context>()).build()
    }

    single<Interceptor>(qualifier = demoServerInterceptor) {
        DemoServerInterceptor(context = get<Context>())
    }
}
