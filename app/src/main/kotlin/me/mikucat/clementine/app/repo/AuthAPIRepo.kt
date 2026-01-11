package me.mikucat.clementine.app.repo

import io.ktor.http.Url
import kotlinx.coroutines.flow.first
import me.mikucat.clementine.PlayAPI

class AuthAPIRepo(
    private val api: PlayAPI,
    private val userData: UserDataRepo,
) {
    suspend fun genLoginUrl(): Result<Url> {
        return api.genGamaLoginURL()
            .onSuccess { (state, _, _) ->
                userData.updateLoginState(state)
            }
            .mapCatching { it.third }
    }

    suspend fun login(state: String, code: String): Result<Unit> {
        val expected = userData.loginState.first()
        if (state != expected) {
            return Result.failure(IllegalArgumentException("login state is different"))
        }
        return api.gamaLogin(code)
            .mapCatching { user -> userData.updateAccount(user) }
    }
}
