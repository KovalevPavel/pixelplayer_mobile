package kov_p.pixelplayer.network.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.scope.Scope

@Suppress("UNUSED_PARAMETER")
internal actual fun createPlatformHttpClient(
    scope: Scope,
    block: HttpClientConfig<*>.() -> Unit,
): HttpClient = HttpClient(Darwin) {
    block()
}
