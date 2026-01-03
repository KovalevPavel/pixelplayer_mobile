package kovp.pixelplayer.core_credentials

import kovp.pixelplayer.api_credentials.CredentialsRepository
import kovp.pixelplayer.api_storage.Preferences

class CredentialsRepositoryImpl(
    private val prefs: Preferences,
) : CredentialsRepository {
    override suspend fun getToken(): String? {
        return prefs.getString(TOKEN_KEY)
    }

    override suspend fun saveToken(token: String) {
        prefs.updateValue(TOKEN_KEY, token)
    }

    override suspend fun getEndpoint(): String? {
        return prefs.getString(ENDPOINT_KEY)
    }

    override suspend fun saveEndpoint(endpoint: String?) {
        prefs.updateValue(ENDPOINT_KEY, endpoint)
    }

    companion object {
        private const val ENDPOINT_KEY = "endpoint"
        private const val TOKEN_KEY = "token"
    }
}
