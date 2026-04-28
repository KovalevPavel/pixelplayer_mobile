package kov_p.pixelplayer.feature_login

import kotlinx.collections.immutable.ImmutableList

sealed interface LoginState {
    data object Init : LoginState

    data class Data(
        val pages: ImmutableList<LoginStep>,
        val initPage: Int,
    ) : LoginState

    sealed interface LoginStep
    data class Endpoint(val initEndpoint: String) : LoginStep
    data object Credentials : LoginStep
}
