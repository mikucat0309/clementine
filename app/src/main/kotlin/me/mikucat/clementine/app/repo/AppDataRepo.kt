package me.mikucat.clementine.app.repo

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.mikucat.clementine.app.model.AppData
import me.mikucat.clementine.app.model.GCMParams

class AppDataRepo(
    private val dataStore: DataStore<AppData>,
) {
    val params: Flow<GCMParams?> = dataStore.data
        .map { it.params }

    suspend fun update(params: GCMParams) {
        dataStore.updateData { it.copy(params = params) }
    }
}
