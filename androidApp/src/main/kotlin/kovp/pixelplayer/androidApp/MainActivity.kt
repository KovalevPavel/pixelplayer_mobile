package kovp.pixelplayer.androidApp

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.delay
import kovp.pixelplayer.App
import kovp.pixelplayer.androidApp.di.androidAppModule
import kovp.pixelplayer.androidApp.di.buildConfigModule
import kovp.pixelplayer.core.context.AndroidAppContext
import kovp.pixelplayer.core_design.Background

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var startupChecksPassed = false
        var isMinimumDurationPending = true

        installSplashScreen().setKeepOnScreenCondition {
            isMinimumDurationPending || !startupChecksPassed
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Background.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Background.toArgb()),
        )
        super.onCreate(savedInstanceState)

        setContent {
            LaunchedEffect(Unit) {
                if (!isMinimumDurationPending || startupChecksPassed) {
                    return@LaunchedEffect
                }

                delay(SPLASH_VISIBLE_DURATION_MS)
                isMinimumDurationPending = false
            }

            App(
                ctx = AndroidAppContext(this@MainActivity.applicationContext),
                onStartupChecksPassed = { startupChecksPassed = true },
                platformModules = listOf(
                    androidAppModule,
                    buildConfigModule,
                ),
            )
        }
    }

    private companion object {
        const val SPLASH_VISIBLE_DURATION_MS = 1_500L
    }
}
