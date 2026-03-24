package kovp.pixelplayer.core_ui.components.message_dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Immutable
data class MessageDialogVs(
    val id: String = "",
    val title: Field? = null,
    val message: Field? = null,
    val primaryAction: StringResource,
    val secondaryAction: StringResource? = null,
) {
    sealed interface Field {
        val isEmpty: Boolean
        val value: String @Composable get

        data class Text(val text: String?) : Field {
            override val isEmpty: Boolean = text.isNullOrEmpty()

            override val value: String @Composable get() = text.orEmpty()
        }

        data class Resource(val res: StringResource) : Field {
            override val isEmpty: Boolean = false
            override val value: String @Composable get() = stringResource(res)
        }
    }
}