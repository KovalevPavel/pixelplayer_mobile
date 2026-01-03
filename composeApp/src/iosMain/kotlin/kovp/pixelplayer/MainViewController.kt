package kovp.pixelplayer

import androidx.compose.ui.window.ComposeUIViewController
import kovp.pixelplayer.di.IosAppContext

fun MainViewController() = ComposeUIViewController { App(ctx = IosAppContext()) }
