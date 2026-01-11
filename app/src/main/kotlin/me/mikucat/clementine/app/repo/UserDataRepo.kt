package me.mikucat.clementine.app.repo

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.mikucat.clementine.GamaAccount
import me.mikucat.clementine.app.model.UserData

class UserDataRepo(
    private val dataStore: DataStore<UserData>,
) {
    val loginState: Flow<String?> = dataStore.data
        .map { it.loginState }

    val account: Flow<GamaAccount?> = dataStore.data
        .map { it.account }

    suspend fun updateLoginState(state: String) {
        dataStore.updateData { it.copy(loginState = state) }
    }

    suspend fun updateAccount(gamaAccount: GamaAccount?) {
        dataStore.updateData { it.copy(account = gamaAccount) }
    }
}
