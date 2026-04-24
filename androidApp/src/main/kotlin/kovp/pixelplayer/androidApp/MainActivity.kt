package kovp.pixelplayer.androidApp

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kovp.pixelplayer.App
import kovp.pixelplayer.androidApp.di.androidAppModule
import kovp.pixelplayer.androidApp.di.buildConfigModule
import kovp.pixelplayer.core.context.AndroidAppContext
import kovp.pixelplayer.core_design.Background

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashStartedAt = SystemClock.elapsedRealtime()
        var startupChecksPassed = false

        installSplashScreen().setKeepOnScreenCondition {
            val isMinimumDurationPending = SystemClock.elapsedRealtime() - splashStartedAt < SPLASH_VISIBLE_DURATION_MS
            isMinimumDurationPending || !startupChecksPassed
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Background.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Background.toArgb()),
        )
        super.onCreate(savedInstanceState)

        setContent {
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
        const val SPLASH_VISIBLE_DURATION_MS = 3_000L
    }
}
