package kovp.pixelplayer

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kovp.pixelplayer.core_ui.CollectWithLifecycle
import kovp.pixelplayer.di.mainModule
import kovp.pixelplayer.main.MainEvent
import kovp.pixelplayer.main.MainViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.Module
import org.koin.dsl.koinConfiguration

private val rippleConfiguration = RippleConfiguration(
    color = Color.White,
)

@Composable
fun App(
    ctx: AppContext,
    platformModules: List<Module> = emptyList(),
    onStartupChecksPassed: () -> Unit = {},
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
                HostComposable(
                    context = ctx,
                    onStartupChecksPassed = onStartupChecksPassed,
                )
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
    onStartupChecksPassed: () -> Unit,
) {

    val hostNavController = rememberNavController()
    val viewModel = koinViewModel<MainViewModel>()
    var startDestination by remember { mutableStateOf<Any?>(null) }

    viewModel.eventsFlow.CollectWithLifecycle { event ->
        when (event) {
            MainEvent.OpenLoginFlow -> {
                startDestination = LoginFlow
            }

            is MainEvent.OpenMainFlow -> {
                startDestination = MainFlow(
                    token = event.token,
                    baseUrl = event.endpoint,
                )
            }

            MainEvent.SplashChecksPassed -> {
                onStartupChecksPassed()
            }
        }
    }

    val startRoute = startDestination ?: return

    NavHost(
        modifier = Modifier.fillMaxSize().safeDrawingPadding(),
        navController = hostNavController,
        startDestination = startRoute,
        enterTransition = { slideInHorizontally { it } },
        exitTransition = { slideOutHorizontally { -it } },
        popEnterTransition = { slideInHorizontally { -it } },
        popExitTransition = { slideOutHorizontally { it } },
    ) {
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

                LoginFlow.let(hostNavController::navigate)
            },
        )
    }
}
