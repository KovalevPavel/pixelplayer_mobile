package kov_p.pixelplayer.network.di

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.LoggingFormat
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.ScopeDSL

private fun HttpClientConfig<*>.defaultLogging() {
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

fun ScopeDSL.bindUnauthorizedClient() {
    scoped(qualifier = unauthorizedClient) {
        createPlatformHttpClient(scope = this) {
            defaultLogging()
            defaultJson()
        }
    }
}

fun Module.bindAuthorizedClient(baseUrl: String, token: String) {
    single(qualifier = authorizedClient) {
        createPlatformHttpClient(scope = this) {
            defaultLogging()
            defaultJson()
            defaultRequest {
                url(baseUrl)
                headers {
                    append("Authorization", token)
                }
            }
        }
    }
}
