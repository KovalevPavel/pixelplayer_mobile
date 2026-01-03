package kovp.pixelplayer.core_storage.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kovp.pixelplayer.core_storage.PrefsType
import okio.Path.Companion.toPath

internal fun createPreferences(
    context: Context,
    type: PrefsType,
    scope: CoroutineScope,
): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            context.filesDir
                .resolve(type.filename)
                .absolutePath
                .toPath()
        },
        scope = scope,
    )
}
