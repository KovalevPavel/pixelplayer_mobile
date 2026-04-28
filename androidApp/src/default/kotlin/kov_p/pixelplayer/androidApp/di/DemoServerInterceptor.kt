package kov_p.pixelplayer.androidApp.di

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

@Suppress("unused")
class DemoServerInterceptor(private val context: Context): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}
