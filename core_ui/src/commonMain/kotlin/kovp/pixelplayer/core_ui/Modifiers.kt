package kovp.pixelplayer.core_ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

class AnimationUtils @OptIn(ExperimentalSharedTransitionApi::class) constructor(
    val sharedTransitionScope: SharedTransitionScope,
    val animatedVisibilityScope: AnimatedVisibilityScope,
)

val LocalAnimationProvider = staticCompositionLocalOf<AnimationUtils?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
fun Modifier.withAnimation(key: Any): Modifier {
    return this.composed {
        val animUtils = LocalAnimationProvider.current
            ?: return@composed this

        return@composed with(animUtils.sharedTransitionScope) {
            this@composed.sharedElement(
                sharedContentState = rememberSharedContentState(key = key),
                animatedVisibilityScope = animUtils.animatedVisibilityScope,
            )
        }
    }
}
