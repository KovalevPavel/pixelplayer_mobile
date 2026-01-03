package kovp.pixelplayer.api_storage

import kotlinx.coroutines.flow.StateFlow

interface Preferences {
    suspend fun getString(key: String): String?
    fun getStringFlow(key: String): StateFlow<String?>
    suspend fun updateValue(key: String, value: String?)
}
