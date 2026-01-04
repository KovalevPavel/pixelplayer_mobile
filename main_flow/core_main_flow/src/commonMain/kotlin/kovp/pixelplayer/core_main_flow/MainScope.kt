package kovp.pixelplayer.core_main_flow

import androidx.compose.runtime.staticCompositionLocalOf
import org.koin.core.scope.Scope

val LocalMainScope = staticCompositionLocalOf<Scope> {
    error("No main flow found")
}
