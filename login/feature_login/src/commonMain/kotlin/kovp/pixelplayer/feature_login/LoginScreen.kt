package kovp.pixelplayer.feature_login

import kotlinx.serialization.Serializable

interface LoginScreen {
    @Serializable
    object Endpoint : LoginScreen

    @Serializable
    object Credentials : LoginScreen
}
