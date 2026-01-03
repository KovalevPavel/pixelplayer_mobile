package kov_p.pixelplayer.network.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.LoggingFormat
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kov_p.pixelplayer.network.ClientWrapper
import kov_p.pixelplayer.network.HttpClientWrapperImpl
import org.koin.dsl.ScopeDSL
import org.koin.dsl.bind

private fun HttpClientConfig<*>.defaultLogging() {
    install(Logging) {
        format = LoggingFormat.OkHttp
        logger = Logger.SIMPLE
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
        HttpClientWrapperImpl(
            client = HttpClient {
                defaultLogging()
                defaultJson()
            }
        )
    }
        .bind<ClientWrapper>()
}

fun ScopeDSL.bindAuthorizedClient(baseUrl: String, token: String) {
    scoped(qualifier = authorizedClient) {
        HttpClientWrapperImpl(
            client = HttpClient {
                defaultLogging()
                defaultJson()
                defaultRequest {
                    url(baseUrl)
                    headers {
                        append("Authorization", token)
                    }
                }
            }
        )
    }
        .bind<ClientWrapper>()
}
