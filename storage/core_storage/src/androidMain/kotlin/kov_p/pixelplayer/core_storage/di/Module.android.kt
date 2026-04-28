package kov_p.pixelplayer.core_storage.di

import androidx.datastore.core.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kov_p.pixelplayer.api_storage.Preferences
import kov_p.pixelplayer.api_storage.SecurePreferences
import kov_p.pixelplayer.core_storage.AndroidSecurePreferences
import kov_p.pixelplayer.core_storage.PreferencesImpl
import kov_p.pixelplayer.core_storage.PrefsType
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
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

    singleOf(::AndroidSecurePreferences).bind<SecurePreferences>()
}
