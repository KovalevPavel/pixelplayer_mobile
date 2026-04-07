package kovp.pixelplayer

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import kovp.pixelplayer.core.language.AppLanguageManager
import kovp.pixelplayer.core.language.AppLanguageRepository
import kovp.pixelplayer.core_storage.di.storageModule
import kovp.pixelplayer.core_ui.CollectWithLifecycle
import kovp.pixelplayer.di.mainModule
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.koinConfiguration

private val rippleConfiguration = RippleConfiguration(
    color = Color.White,
)

@Composable
fun App(
    ctx: AppContext,
) {
    KoinApplication(
        configuration = koinConfiguration {
            bindContext(ctx)
            modules(
                storageModule,
                mainModule,
                credentialsModule,
                languageModule,
            )
        },
    ) {
        val languageManager = koinInject<AppLanguageManager>()
        val languageRepository = koinInject<AppLanguageRepository>()
        var isLanguageReady by remember {
            mutableStateOf(!languageManager.supportsOverride)
        }

        LaunchedEffect(languageManager, languageRepository) {
            if (languageManager.supportsOverride) {
                languageManager.applySelection(languageRepository.getSelection())
            }
            isLanguageReady = true
        }

        if (!isLanguageReady) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            return@KoinApplication
        }

        AppTheme {
            val viewModel = koinViewModel<MainViewModel>()
            var checkResult: MainEvent.CheckResult? by rememberSaveable(
                viewModel,
                stateSaver = CheckResultStateSaver(),
            ) {
                mutableStateOf(null)
            }

            viewModel.event.CollectWithLifecycle { event ->
                when (event) {
                    is MainEvent.LaunchMainHost -> {
                        checkResult = event.result
                    }
                }
            }

            CompositionLocalProvider(
                LocalRippleConfiguration provides rippleConfiguration,
            ) {
                HostComposable(result = checkResult, context = ctx) {
                    checkResult = null
                    MainAction.CheckCredentials.let(viewModel::handleAction)
                }
            }
        }
    }
}

@Composable
private fun HostComposable(
    result: MainEvent.CheckResult?,
    context: AppContext,
    refreshCredentials: () -> Unit,
) {
    if (result == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }

    val startDestination = when (result) {
        null -> return
        MainEvent.CheckResult.EmptyEndpoint,
        MainEvent.CheckResult.EmptyCreds,
        -> {
            LoginFlow
        }

        is MainEvent.CheckResult.OpenMain -> {
            MainFlow(
                token = result.token,
                baseUrl = result.endpoint,
            )
        }
    }

    val hostNavController = rememberNavController()

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = hostNavController,
        startDestination = startDestination,
        enterTransition = { slideInHorizontally { it } },
        exitTransition = { slideOutHorizontally { -it } },
        popEnterTransition = { slideInHorizontally { -it } },
        popExitTransition = { slideOutHorizontally { it } },
    ) {
        registerLoginFlow(
            endpointIsEmpty = result == MainEvent.CheckResult.EmptyEndpoint,
            navController = hostNavController,
            onTokenSaved = { token, endpoint ->
                hostNavController.popBackStack(
                    route = LoginFlow,
                    inclusive = true,
                    saveState = false,
                )
                MainFlow(
                    token = token,
                    baseUrl = endpoint,
                )
                    .let(hostNavController::navigate)
            },
        )
        registerMainFlow(
            ctx = context,
            onLogout = refreshCredentials,
        )
    }
}
