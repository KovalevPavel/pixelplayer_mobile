package kov_p.pixelplayer

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kov_p.pixelplayer.api_login.LoginFlow
import kov_p.pixelplayer.api_login.registerLoginFlow
import kov_p.pixelplayer.api_main_flow.MainFlow
import kov_p.pixelplayer.api_main_flow.registerMainFlow
import kov_p.pixelplayer.api_settings.di.languageModule
import kov_p.pixelplayer.core.context.AppContext
import kov_p.pixelplayer.core.context.bindContext
import kov_p.pixelplayer.core_credentials.credentialsModule
import kov_p.pixelplayer.core_design.AppTheme
import kov_p.pixelplayer.core_storage.di.storageModule
import kov_p.pixelplayer.di.mainModule
import kov_p.pixelplayer.main.MainViewModel
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
        val viewModel = koinViewModel<MainViewModel>()
        val startDestination by viewModel.startDestinationFlow.collectAsState()

        LaunchedEffect(startDestination) {
            if (startDestination != null) {
                onStartupChecksPassed()
            }
        }

        AppTheme {
            CompositionLocalProvider(
                LocalRippleConfiguration provides rippleConfiguration,
            ) {
                HostComposable(
                    context = ctx,
                    startRoute = startDestination ?: return@CompositionLocalProvider,
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
    startRoute: Any,
) {
    val hostNavController = rememberNavController()

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
