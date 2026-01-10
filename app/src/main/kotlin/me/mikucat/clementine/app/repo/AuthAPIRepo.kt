package me.mikucat.clementine.app.repo

import io.ktor.http.Url
import kotlinx.coroutines.flow.first
import me.mikucat.clementine.PlayAPI

class AuthAPIRepo(
    private val api: PlayAPI,
    private val appData: AppDataRepo,
) {
    suspend fun genLoginUrl(): Result<Url> {
        return api.genGamaLoginURL()
            .onSuccess { (state, _, _) ->
                appData.updateLoginState(state)
            }
            .mapCatching { it.third }
    }

    suspend fun login(state: String, code: String): Result<Unit> {
        val expected = appData.loginState.first()
        if (state != expected) {
            return Result.failure(IllegalArgumentException("login state is different"))
        }
        return api.gamaLogin(code)
            .mapCatching { user -> appData.updateAccount(user) }
    }
}
