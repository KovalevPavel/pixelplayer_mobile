package kov_p.pixelplayer.api_storage

interface SecurePreferences {
    suspend fun getString(key: String): String?
    suspend fun updateValue(key: String, value: String?)
}
