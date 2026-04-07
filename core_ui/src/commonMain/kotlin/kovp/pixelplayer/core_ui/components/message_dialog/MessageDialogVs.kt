package kovp.pixelplayer.core_ui.components.message_dialog

import androidx.compose.runtime.Immutable
import kovp.pixelplayer.core_ui.UiText

@Immutable
data class MessageDialogVs(
    val id: String = "",
    val title: UiText? = null,
    val message: UiText? = null,
    val primaryAction: UiText,
    val secondaryAction: UiText? = null,
)
