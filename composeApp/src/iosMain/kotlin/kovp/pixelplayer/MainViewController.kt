package kovp.pixelplayer

import androidx.compose.ui.window.ComposeUIViewController
import kovp.pixelplayer.core.context.IosAppContext

fun MainViewController() = ComposeUIViewController { App(ctx = IosAppContext()) }
