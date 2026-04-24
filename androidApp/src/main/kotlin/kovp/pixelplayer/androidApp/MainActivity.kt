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
        installSplashScreen().setKeepOnScreenCondition {
            SystemClock.elapsedRealtime() - splashStartedAt < SPLASH_VISIBLE_DURATION_MS
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Background.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Background.toArgb()),
        )
        super.onCreate(savedInstanceState)

        setContent {
            App(
                ctx = AndroidAppContext(this@MainActivity.applicationContext),
                platformModules = listOf(
                    androidAppModule,
                    buildConfigModule,
                ),
            )
        }
    }

    private companion object {
        const val SPLASH_VISIBLE_DURATION_MS = 6_000L
    }
}
