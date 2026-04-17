package kovp.pixelplayer

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kovp.pixelplayer.api_login.LoginFlow
import kovp.pixelplayer.api_login.registerLoginFlow
import kovp.pixelplayer.api_main_flow.MainFlow
import kovp.pixelplayer.api_main_flow.registerMainFlow
import kovp.pixelplayer.api_settings.di.languageModule
import kovp.pixelplayer.core.context.AppContext
import kovp.pixelplayer.core.context.bindContext
import kovp.pixelplayer.core_credentials.credentialsModule
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_storage.di.storageModule
import kovp.pixelplayer.di.mainModule
import kovp.pixelplayer.initializer.Initializer
import kovp.pixelplayer.initializer.registerInitializer
import org.koin.compose.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.koinConfiguration

private val rippleConfiguration = RippleConfiguration(
    color = Color.White,
)

@Composable
fun App(
    ctx: AppContext,
    platformModules: List<Module> = emptyList(),
) {
    KoinApplication(
        configuration = koinConfiguration {
            bindContext(ctx)
            modules(
                storageModule,
                mainModule,
                credentialsModule,
                languageModule,
                *platformModules.toTypedArray(),
            )
        },
    ) {
        AppTheme {
            CompositionLocalProvider(
                LocalRippleConfiguration provides rippleConfiguration,
            ) {
                HostComposable(context = ctx)
            }
        }
    }
}

private inline fun <reified T: Any> NavHostController.navigateToMainFlow(token: String, endpoint: String) {
    this.popBackStack<T>(
        inclusive = true,
        saveState = false,
    )

    MainFlow(
        token = token,
        baseUrl = endpoint,
    )
        .let(this::navigate)
}

@Composable
private fun HostComposable(
    context: AppContext,
) {

    val hostNavController = rememberNavController()

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = hostNavController,
        startDestination = Initializer,
        enterTransition = { slideInHorizontally { it } },
        exitTransition = { slideOutHorizontally { -it } },
        popEnterTransition = { slideInHorizontally { -it } },
        popExitTransition = { slideOutHorizontally { it } },
    ) {
        registerInitializer(
            navigateToLoginFlow = {
                hostNavController.popBackStack(
                    route = Initializer,
                    inclusive = true,
                    saveState = false,
                )

                LoginFlow.let(hostNavController::navigate)
            },
            navigateToMainFlow = { token, endpoint ->
                hostNavController.navigateToMainFlow<Initializer>(
                    token = token,
                    endpoint = endpoint,
                )
            }
        )

        registerLoginFlow(
            navigateToMainFlow = { token, endpoint ->
                hostNavController.navigateToMainFlow<LoginFlow>(token = token, endpoint = endpoint)
            },
        )

        registerMainFlow(
            ctx = context,
            onLogout = {
                hostNavController.popBackStack<MainFlow>(
                    inclusive = true,
                    saveState = false,
                )

                Initializer.let(hostNavController::navigate)
            },
        )
    }
}
