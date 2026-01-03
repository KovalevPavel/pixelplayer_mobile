package kovp.pixelplayer.core_ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun <T> Flow<T>.CollectWithLifecycle(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    action: (T) -> Unit,
) {
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        this@CollectWithLifecycle.flowWithLifecycle(
            lifecycle = lifecycle,
            minActiveState = minActiveState,
        )
            .onEach { action(it) }
            .launchIn(scope)
        onDispose { }
    }
}
