package kovp.pixelplayer.api_login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import kovp.pixelplayer.api_login.di.LoginScope
import kovp.pixelplayer.api_login.di.loginFlowModule
import kovp.pixelplayer.feature_login.ui.LoginComposable
import org.koin.compose.getKoin
import org.koin.core.scope.Scope

@Serializable
object LoginFlow

fun NavGraphBuilder.registerLoginFlow(
    navigateToMainFlow: (token: String, endpoint: String) -> Unit,
) {
    composable<LoginFlow> {
        val scope = rememberLoginScope()

        LoginComposable(
            scope = scope,
            navigateToMainFlow = navigateToMainFlow,
        )

        DisposableEffect(scope) {
            onDispose {
                runCatching { scope.close() }
            }
        }
    }
}

@Composable
private fun rememberLoginScope(): Scope {
    val koin = getKoin()

    return remember {
        koin.loadModules(modules = listOf(loginFlowModule))
        koin.getOrCreateScope<LoginScope>(LoginScope.toString())
    }
}
