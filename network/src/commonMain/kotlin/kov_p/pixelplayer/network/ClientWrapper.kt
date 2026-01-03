package kov_p.pixelplayer.network

import kotlin.reflect.KClass

interface ClientWrapper {
    @InternalCoreNetworkAPI
    suspend fun <T : Any> getInternal(
        type: KClass<T>,
        url: String?,
        path: String,
        params: Map<String, String>,
    ): T

    @InternalCoreNetworkAPI
    suspend fun <T : Any> postInternal(
        type: KClass<T>,
        url: String?,
        path: String,
        params: Map<String, String>,
    ): T
}

@Suppress("ExperimentalAnnotationRetention")
@RequiresOptIn(
    message = "This API is internal in network module and should not be used outside",
    level = RequiresOptIn.Level.ERROR,
)
annotation class InternalCoreNetworkAPI
