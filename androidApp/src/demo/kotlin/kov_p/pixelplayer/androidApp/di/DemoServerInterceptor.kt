package kov_p.pixelplayer.androidApp.di

import android.content.Context
import kotlinx.io.IOException
import kov_p.pixelplayer.R
import okhttp3.Interceptor
import okhttp3.Response

class DemoServerInterceptor(
    private val context: Context,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val demoServerIp = context.getString(R.string.demo_server_ip)

        val originalRequest = chain.request()

        return when (originalRequest.url.host) {
            demoServerIp -> chain.proceed(originalRequest)
            else -> throw IOException("not demo app url")
        }
    }
}
