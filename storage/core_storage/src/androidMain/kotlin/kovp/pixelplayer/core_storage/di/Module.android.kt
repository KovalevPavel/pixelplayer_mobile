package kovp.pixelplayer.core_storage.di

import androidx.datastore.core.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kovp.pixelplayer.api_storage.Preferences
import kovp.pixelplayer.core_storage.PreferencesImpl
import kovp.pixelplayer.core_storage.PrefsType
import org.koin.core.module.Module
import org.koin.dsl.module

actual val storageModule: Module = module {
    var prefs: DataStore<androidx.datastore.preferences.core.Preferences>? = null

    single<Preferences>(createdAtStart = true) {
        val scope = CoroutineScope(context = Dispatchers.IO)
        PreferencesImpl(
            prefs = prefs
                ?: createPreferences(
                    context = get(),
                    type = PrefsType.Authentication,
                    scope = scope,
                )
                    .also { prefs = it },
            scope = scope,
        )
    }
}
