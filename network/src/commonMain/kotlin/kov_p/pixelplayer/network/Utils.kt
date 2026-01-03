package kov_p.pixelplayer.network

@OptIn(InternalCoreNetworkAPI::class)
suspend inline fun <reified T : Any> ClientWrapper.get(
    url: String,
    path: String = "",
    params: Map<String, String> = emptyMap(),
): T {
    return this.getInternal(
        type = T::class,
        url = url,
        path = path,
        params = params,
    )
}

@OptIn(InternalCoreNetworkAPI::class)
suspend inline fun <reified T : Any> ClientWrapper.post(
    url: String,
    path: String = "",
    params: Map<String, String> = emptyMap(),
): T {
    return this.postInternal(
        type = T::class,
        url = url,
        path = path,
        params = params,
    )
}

@OptIn(InternalCoreNetworkAPI::class)
suspend inline fun <reified T : Any> ClientWrapper.get(
    path: String = "",
    params: Map<String, String> = emptyMap(),
): T {
    return this.getInternal(
        type = T::class,
        url = null,
        path = path,
        params = params,
    )
}
