package kov_p.pixelplayer.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.appendPathSegments
import io.ktor.util.reflect.TypeInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

class HttpClientWrapperImpl(
    private val client: HttpClient,
) : ClientWrapper {
    @InternalCoreNetworkAPI
    override suspend fun <T : Any> getInternal(
        type: KClass<T>,
        url: String?,
        path: String,
        params: Map<String, String>,
    ): T {
        val response = client.get {
            url?.let { url(it) }

            url {
                appendPathSegments("api", path)
                params.forEach { (k, v) -> parameter(k, v) }
            }
        }

        return mapResponse(type = type, response = response)
    }

    @InternalCoreNetworkAPI
    override suspend fun <T : Any> postInternal(
        type: KClass<T>,
        url: String?,
        path: String,
        params: Map<String, String>
    ): T {
        val response = client.post {
            url?.let { url(it) }

            url {
                appendPathSegments("api", path)
                params.forEach { (k, v) -> parameter(k, v) }
            }
        }

        return mapResponse(type = type, response = response)
    }

    private suspend fun <T : Any> mapResponse(
        type: KClass<T>,
        response: HttpResponse,
    ): T {
        return when (response.status.value) {
            in 200..299 -> {
                response.body(typeInfo = TypeInfo(type = type))
            }

            else -> {
                val jsonString = response.bodyAsText()
                val error = Json.decodeFromString<ErrorWrapper>(jsonString)
                error(error.message)
            }
        }
    }
}

@Serializable
private class ErrorWrapper(
    @SerialName("error")
    val message: String,
)
