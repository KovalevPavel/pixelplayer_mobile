package kovp.pixelplayer.feature_settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kovp.pixelplayer.core.language.AppLanguage
import kovp.pixelplayer.core.language.LanguageSelection
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_design.pixelColors
import kovp.pixelplayer.feature_settings.presentation.SettingsAction
import org.jetbrains.compose.resources.stringResource
import pixelplayer.core_ui.generated.resources.Res
import pixelplayer.core_ui.generated.resources.system

@Composable
fun LanguageDialog(
    languageSelection: LanguageSelection,
    deviceLanguage: AppLanguage,
    onAction: (SettingsAction) -> Unit,
) {
    LanguageSurface(
        text = getSystemLanguageString(deviceLanguage),
        isSelected = languageSelection is LanguageSelection.System,
        onCLick = {
            onAction(SettingsAction.ChangeLanguage(LanguageSelection.System))
        },
    )
    AppLanguage.entries.forEach {
        LanguageSurface(
            text = it.toStringRes(),
            isSelected = (languageSelection as? LanguageSelection.Explicit)?.language == it,
            onCLick = {
                val action = LanguageSelection.Explicit(it)
                    .let(SettingsAction::ChangeLanguage)
                onAction(action)
            },
        )
    }
}

@Composable
private fun LanguageSurface(
    text: String,
    isSelected: Boolean,
    onCLick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent,
        enabled = !isSelected,
        onClick = onCLick,
    ) {
        Text(
            modifier = Modifier.padding(all = 16.dp),
            text = text,
            color = if (isSelected) pixelColors.primary else pixelColors.onSurface,
        )
    }
}

@Composable
private fun getSystemLanguageString(
    deviceLanguage: AppLanguage,
): String {
    val effectiveString = deviceLanguage.toStringRes()

    return "${stringResource(Res.string.system)} ($effectiveString)"
}

@AppPreview
@Composable
private fun LanguageDialogPreview() {
    AppTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            LanguageDialog(
                languageSelection = LanguageSelection.System,
                deviceLanguage = AppLanguage.English,
                onAction = {},
            )
        }
    }
}
