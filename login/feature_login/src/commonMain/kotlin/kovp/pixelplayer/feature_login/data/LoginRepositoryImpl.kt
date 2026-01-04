package kovp.pixelplayer.feature_login.data

import io.ktor.client.HttpClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kov_p.pixelplayer.network.get
import kov_p.pixelplayer.network.post
import kovp.pixelplayer.api_credentials.CredentialsRepository
import kovp.pixelplayer.domain_login.LoginRepository

class LoginRepositoryImpl(
    private val client: HttpClient,
    private val credentialsRepository: CredentialsRepository,
) : LoginRepository {
    override suspend fun checkEndpoint(endpoint: String): Boolean {
        return client.get<ValidationDto>(
            url = endpoint,
            path = "validate",
        )
            .result == "ok"
    }

    override suspend fun login(login: String, password: String): String {
        return client.post(
            url = credentialsRepository.getEndpoint().orEmpty(),
            path = "login",
            params = mapOf(
                "login" to login,
                "password" to password,
            )
        )
    }
}

@Serializable
private class ValidationDto(
    @SerialName("validation")
    val result: String?
)
