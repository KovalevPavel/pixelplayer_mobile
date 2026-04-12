package kovp.pixelplayer.androidApp

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.toArgb
import kovp.pixelplayer.App
import kovp.pixelplayer.core.context.AndroidAppContext
import kovp.pixelplayer.core_design.Background
import kovp.pixelplayer.androidApp.di.androidAppModule

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Background.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Background.toArgb()),
        )
        super.onCreate(savedInstanceState)

        setContent {
            App(
                ctx = AndroidAppContext(this@MainActivity.applicationContext),
                platformModules = listOf(androidAppModule),
            )
        }
    }
}
