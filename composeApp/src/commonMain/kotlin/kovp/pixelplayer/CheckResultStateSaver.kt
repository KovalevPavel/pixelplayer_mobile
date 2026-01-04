package kovp.pixelplayer

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope

class CheckResultStateSaver: Saver<MainEvent.CheckResult?, String> {
    override fun SaverScope.save(value: MainEvent.CheckResult?): String? {
        return when(value) {
            is MainEvent.CheckResult.EmptyCreds -> EMPTY_CREDS
            is MainEvent.CheckResult.EmptyEndpoint -> EMPTY_ENDPOINT
            is MainEvent.CheckResult.OpenMain -> "${value.token}$CREDS_SPITTER${value.endpoint}"
            null -> ""
        }
    }

    override fun restore(value: String): MainEvent.CheckResult? {
        val creds = value.split(CREDS_SPITTER)
        if (creds.size == 2) {
            return MainEvent.CheckResult.OpenMain(
                token = creds[0],
                endpoint = creds[1],
            )
        }

        return when(value) {
            EMPTY_ENDPOINT -> MainEvent.CheckResult.EmptyEndpoint
            EMPTY_CREDS -> MainEvent.CheckResult.EmptyCreds
            else -> null
        }
    }

    companion object {
        private const val EMPTY_ENDPOINT = ""
        private const val EMPTY_CREDS = ""
        private const val CREDS_SPITTER = "%%%"
    }
}
