package kovp.pixelplayer.api_login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import kovp.pixelplayer.api_login.di.LoginScope
import kovp.pixelplayer.api_login.di.loginFlowModule
import kovp.pixelplayer.feature_login.LoginScreen
import kovp.pixelplayer.feature_login.ui.CredentialsComposable
import kovp.pixelplayer.feature_login.ui.EndpointComposable
import org.koin.compose.getKoin
import org.koin.core.scope.Scope

@Serializable
object LoginFlow

fun NavGraphBuilder.registerLoginFlow(
    endpointIsEmpty: Boolean,
    navController: NavHostController,
    onTokenSaved: (token: String, endpoint: String) -> Unit,
) {
    val startDestination = if (endpointIsEmpty) {
        LoginScreen.Endpoint
    } else {
        LoginScreen.Credentials
    }

    navigation<LoginFlow>(startDestination = startDestination) {
        composable<LoginScreen.Endpoint> {
            EndpointComposable(
                scope = rememberLoginScope(),
                onEndpointSaved = {
                    navController.navigate(route = LoginScreen.Credentials)
                },
            )
        }

        composable<LoginScreen.Credentials> {
            val scope = rememberLoginScope()

            CredentialsComposable(
                scope = scope,
                onTokenSaved = { token, endpoint ->
                    onTokenSaved(token, endpoint)
                    scope.close()
                },
                onChangeEndpoint = {
                    navController.previousBackStackEntry?.let {
                        println("navigate up")
                        navController.navigateUp()
                    }
                        ?: run {
                            println("reset")
                            navController.popBackStack(
                                route = LoginScreen.Credentials,
                                inclusive = true,
                                saveState = false,
                            )
                            navController.navigate(LoginScreen.Endpoint)
                        }
                },
            )
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
