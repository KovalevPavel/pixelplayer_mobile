package kovp.pixelplayer.domain_login

interface LoginRepository {
    suspend fun checkEndpoint(endpoint: String): Boolean
    suspend fun login(login: String, password: String): String
}
