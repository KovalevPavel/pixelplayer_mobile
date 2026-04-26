package kov_p.pixelplayer.network.di

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.LoggingFormat
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kovp.pixelplayer.core.build_config.BuildConfig
import org.koin.core.module.Module
import org.koin.dsl.ScopeDSL

private const val DEFAULT_CONNECTION_TIMEOUT_MS = 5000L

private fun HttpClientConfig<*>.defaultLogging(enabled: Boolean) {
    if (!enabled) return

    install(Logging) {
        format = LoggingFormat.OkHttp
        logger = Logger.SIMPLE
        level = LogLevel.ALL
    }
}

private fun HttpClientConfig<*>.defaultJson() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }
}

private fun HttpClientConfig<*>.defaultTimeout() {
    install(HttpTimeout) {
        connectTimeoutMillis = DEFAULT_CONNECTION_TIMEOUT_MS
    }
}

fun ScopeDSL.bindUnauthorizedClient() {
    scoped(qualifier = unauthorizedClient) {
        val enableHttpLogging = getOrNull<BuildConfig>()?.isDebug == true

        createPlatformHttpClient(scope = this) {
            defaultLogging(enabled = enableHttpLogging)
            defaultJson()
            defaultTimeout()
        }
    }
}

fun Module.bindAuthorizedClient(baseUrl: String, token: String) {
    single(qualifier = authorizedClient) {
        val enableHttpLogging = getOrNull<BuildConfig>()?.isDebug == true

        createPlatformHttpClient(scope = this) {
            defaultLogging(enabled = enableHttpLogging)
            defaultJson()
            defaultTimeout()

            defaultRequest {
                url(baseUrl)
                headers {
                    append("Authorization", token)
                }
            }
        }
    }
}
