package kov_p.pixelplayer.network.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.Interceptor
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

internal actual fun createPlatformHttpClient(
    scope: Scope,
    block: HttpClientConfig<*>.() -> Unit,
): HttpClient {
    return HttpClient(OkHttp) {
        block()
        engine {
            config {
                scope.getInterceptor(qualifier = okHttpInterceptor)?.let(::addInterceptor)
                scope.getInterceptor(qualifier = demoServerInterceptor)?.let(::addInterceptor)
            }
        }
    }
}

private fun Scope.getInterceptor(qualifier: Qualifier): Interceptor? {
    return runCatching { this@getInterceptor.get<Interceptor>(qualifier = qualifier) }
        .getOrNull()
}
