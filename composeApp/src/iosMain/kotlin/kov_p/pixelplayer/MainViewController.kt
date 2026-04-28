package kov_p.pixelplayer

import androidx.compose.ui.window.ComposeUIViewController
import kov_p.pixelplayer.core.context.IosAppContext

fun MainViewController() = ComposeUIViewController { App(ctx = IosAppContext()) }
