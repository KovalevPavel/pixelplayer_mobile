package kov_p.pixelplayer.api_main_flow.di

import kov_p.pixelplayer.network.di.bindAuthorizedClient
import kov_p.pixelplayer.feature_main_flow.presentation.MainFlowViewModel
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.module

object MainFlowScope

fun mainFlowModule(token: String, baseUrl: String) = module {
    bindAuthorizedClient(token = token, baseUrl = baseUrl)

    scope<MainFlowScope> {
        scopedOf(::MainFlowViewModel)
    }
}
