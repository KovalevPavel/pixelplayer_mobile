package kovp.pixelplayer.api_main_flow

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import kovp.pixelplayer.feature_main_flow.MainFlowScreen

@Serializable
object MainFlow

fun NavGraphBuilder.registerMainFlow() {
    composable<MainFlow> {
        val mainFlowNavController = rememberNavController()

        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = mainFlowNavController,
            startDestination = MainFlowScreen.entries.first().name,
        ) {
            MainFlowScreen.entries
                .forEach { e ->
                    composable(e.name) {
                        when (e) {
                            MainFlowScreen.Artists -> {

                            }

                            MainFlowScreen.Albums -> {

                            }

                            MainFlowScreen.Tracks -> {

                            }
                        }
                    }
                }
        }
    }
}
