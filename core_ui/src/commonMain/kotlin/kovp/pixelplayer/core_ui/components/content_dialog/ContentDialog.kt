package kovp.pixelplayer.core_ui.components.content_dialog

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kovp.pixelplayer.core_design.pixelColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentDialog(
    removeFromComposition: () -> Unit,
    content: (@Composable ColumnScope.(scope: CoroutineScope, state: SheetState) -> Unit),
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        modifier = Modifier.fillMaxWidth()
            .wrapContentHeight(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .7f),
            )
        },
        scrimColor = pixelColors.scrim.copy(alpha = .32f),
        sheetState = sheetState,
        onDismissRequest = removeFromComposition,
        content = { content(scope, sheetState) },
    )
}
