package kovp.pixelplayer.api_credentials

interface CredentialsRepository {
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
    suspend fun getEndpoint(): String?
    suspend fun saveEndpoint(endpoint: String?)
}
