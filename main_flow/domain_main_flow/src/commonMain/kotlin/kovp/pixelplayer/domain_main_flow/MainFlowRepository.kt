package kovp.pixelplayer.domain_main_flow

interface MainFlowRepository {
    suspend fun checkEndpoint(): Boolean
    suspend fun checkCredentials(): Boolean
}
