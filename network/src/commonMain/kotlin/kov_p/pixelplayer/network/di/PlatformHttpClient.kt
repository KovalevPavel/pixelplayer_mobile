package kov_p.pixelplayer.network.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import org.koin.core.scope.Scope

internal expect fun createPlatformHttpClient(
    scope: Scope,
    block: HttpClientConfig<*>.() -> Unit,
): HttpClient
