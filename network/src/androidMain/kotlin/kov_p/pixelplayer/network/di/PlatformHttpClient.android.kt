package kov_p.pixelplayer.network.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.Interceptor
import org.koin.core.scope.Scope

internal actual fun createPlatformHttpClient(
    scope: Scope,
    block: HttpClientConfig<*>.() -> Unit,
): HttpClient {
    val interceptor = runCatching {
        scope.get<Interceptor>(qualifier = okHttpInterceptor)
    }
        .getOrNull()

    return HttpClient(OkHttp) {
        block()
        engine {
            config {
                interceptor?.let(::addInterceptor)
            }
        }
    }
}
