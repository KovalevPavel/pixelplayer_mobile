package kovp.pixelplayer.core_storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kovp.pixelplayer.api_storage.Preferences

internal class PreferencesImpl(
    private val prefs: DataStore<androidx.datastore.preferences.core.Preferences>,
    private val scope: CoroutineScope,
) : Preferences {
    override suspend fun getString(key: String): String? {
        val k = stringPreferencesKey(key)
        return prefs.data.firstOrNull()?.get(k)
    }

    override fun getStringFlow(key: String): StateFlow<String?> {
        val k = stringPreferencesKey(key)
        return prefs.data
            .map { it[k] }
            .stateIn(scope = scope, started = SharingStarted.Eagerly, initialValue = null)
    }

    override suspend fun updateValue(key: String, value: String?) {
        val k = stringPreferencesKey(key)

        prefs.updateData {
            it.toMutablePreferences().also { prefs ->
                if (value == null) {
                    prefs.remove(k)
                } else {
                    prefs[k] = value
                }
            }
        }
    }
}
