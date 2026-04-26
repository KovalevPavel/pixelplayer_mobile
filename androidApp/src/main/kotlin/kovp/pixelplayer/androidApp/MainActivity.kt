package kovp.pixelplayer.androidApp

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kovp.pixelplayer.App
import kovp.pixelplayer.androidApp.di.androidAppModule
import kovp.pixelplayer.androidApp.di.buildConfigModule
import kovp.pixelplayer.core.context.AndroidAppContext
import kovp.pixelplayer.core_design.Background

class MainActivity : AppCompatActivity() {
    private val isSplashVisible = MutableStateFlow(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { isSplashVisible.value }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Background.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Background.toArgb()),
        )
        super.onCreate(savedInstanceState)

        setContent {
            App(
                ctx = AndroidAppContext(this@MainActivity.applicationContext),
                onStartupChecksPassed = {
                    isSplashVisible.update { false }
                },
                platformModules = listOf(
                    androidAppModule,
                    buildConfigModule,
                ),
            )
        }
    }

    override fun onDestroy() {
        isSplashVisible.update { false }
        super.onDestroy()
    }
}
