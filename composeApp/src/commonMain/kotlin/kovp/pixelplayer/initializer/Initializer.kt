package kovp.pixelplayer.initializer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import kovp.pixelplayer.core_ui.CollectWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object Initializer

fun NavGraphBuilder.registerInitializer(
    navigateToLoginFlow: () -> Unit,
    navigateToMainFlow: (token: String, endpoint: String) -> Unit,
) {
    composable<Initializer> {
        val viewModel = koinViewModel<InitializerViewModel>()

        viewModel.eventsFlow.CollectWithLifecycle { event ->
            when (event) {
                is InitializerEvent.OpenLoginFlow -> {
                    navigateToLoginFlow()
                }

                is InitializerEvent.OpenMainFlow -> {
                    navigateToMainFlow(event.token, event.endpoint)
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}
