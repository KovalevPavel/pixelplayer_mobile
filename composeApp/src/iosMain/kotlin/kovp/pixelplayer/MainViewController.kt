package kovp.pixelplayer

import androidx.compose.ui.window.ComposeUIViewController
import kovp.pixelplayer.core_player.context.IosAppContext

fun MainViewController() = ComposeUIViewController { App(ctx = IosAppContext()) }
