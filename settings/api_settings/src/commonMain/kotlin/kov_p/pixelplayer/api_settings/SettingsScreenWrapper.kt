package kov_p.pixelplayer.api_settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kov_p.pixelplayer.api_settings.di.SettingsScope
import kov_p.pixelplayer.api_settings.di.settingsScopeModule
import kov_p.pixelplayer.feature_settings.ui.SettingsScreenComposable
import org.koin.compose.getKoin

@Composable
fun SettingsScreenWrapper(
    onLogout: () -> Unit,
) {
    val koin = getKoin()

    val scope = remember {
        koin.loadModules(listOf(settingsScopeModule))
        koin.getOrCreateScope<SettingsScope>(SettingsScope.toString())
    }

    SettingsScreenComposable(
        scope = scope,
        onLogout = onLogout,
    )
}
