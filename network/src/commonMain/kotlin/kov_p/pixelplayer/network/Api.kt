package kov_p.pixelplayer.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.appendPathSegments

suspend inline fun <reified T : Any> HttpClient.get(
    path: String = "",
    params: Map<String, String> = emptyMap(),
): T {
    val response = this.get {
        url {
            appendPathSegments("api", path)
            params.forEach { (k, v) -> parameter(k, v) }
        }
    }

    return mapResponse<T>(response = response)
}

suspend inline fun <reified T : Any> HttpClient.get(
    url: String,
    path: String = "",
    params: Map<String, String> = emptyMap(),
): T {
    val response = this.get {
        url(url)

        url {
            appendPathSegments("api", path)
            params.forEach { (k, v) -> parameter(k, v) }
        }
    }

    return mapResponse<T>(response = response)
}

suspend inline fun <reified T : Any> HttpClient.post(
    path: String = "",
    params: Map<String, String> = emptyMap(),
): T {
    val response = this.post {
        url {
            appendPathSegments("api", path)
            params.forEach { (k, v) -> parameter(k, v) }
        }
    }

    return mapResponse<T>(response = response)
}

suspend inline fun <reified T : Any> HttpClient.post(
    url: String,
    path: String = "",
    params: Map<String, String> = emptyMap(),
): T {
    val response = this.post {
        url(url)

        url {
            appendPathSegments("api", path)
            params.forEach { (k, v) -> parameter(k, v) }
        }
    }

    return mapResponse<T>(response = response)
}
