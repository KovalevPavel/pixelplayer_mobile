package kovp.pixelplayer.core_ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kovp.pixelplayer.core_design.scrimLight

@Composable
fun FullScreenLoader(
    initDelay: Long = 200,
) {
    var isLoaderVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (isLoaderVisible) {
            return@LaunchedEffect
        }

        delay(initDelay)
        isLoaderVisible = true
    }

    if (isLoaderVisible) {
        Box(
            modifier = Modifier
                .background(color = scrimLight.copy(alpha = .2f))
                .fillMaxSize()
                .pointerInput(Unit) {},
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}
