package kov_p.pixelplayer.core_credentials

import kov_p.pixelplayer.api_credentials.CredentialsRepository
import kov_p.pixelplayer.api_storage.SecurePreferences

class CredentialsRepositoryImpl(
    private val prefs: SecurePreferences,
) : CredentialsRepository {
    override suspend fun getToken(): String? {
        return prefs.getString(TOKEN_KEY)
    }

    override suspend fun saveToken(token: String?) {
        prefs.updateValue(TOKEN_KEY, token)
    }

    override suspend fun getUsername(): String? {
        return prefs.getString(USERNAME_KEY)
    }

    override suspend fun saveUsername(username: String?) {
        prefs.updateValue(USERNAME_KEY, username)
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
        private const val USERNAME_KEY = "username"
    }
}
