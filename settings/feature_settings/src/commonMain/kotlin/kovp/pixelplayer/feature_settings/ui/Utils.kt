package kovp.pixelplayer.feature_settings.ui

import androidx.compose.runtime.Composable
import kovp.pixelplayer.core.language.AppLanguage
import org.jetbrains.compose.resources.stringResource
import pixelplayer.core_ui.generated.resources.Res as coreRes
import pixelplayer.core_ui.generated.resources.english
import pixelplayer.core_ui.generated.resources.german
import pixelplayer.core_ui.generated.resources.russian

@Composable
fun AppLanguage.toStringRes(): String {
    val res = when (this) {
        AppLanguage.English -> coreRes.string.english
        AppLanguage.Russian -> coreRes.string.russian
        AppLanguage.German -> coreRes.string.german
    }

    return stringResource(res)
}
