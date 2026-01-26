package me.mikucat.clementine.app.repo

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.mikucat.clementine.app.model.AppData
import me.mikucat.clementine.app.model.GCMParams

class AppDataRepo(
    private val dataStore: DataStore<AppData>,
) {
    val gcmParams: Flow<GCMParams?> = dataStore.data
        .map { it.gcmParams }

    val onboarding: Flow<Boolean?> = dataStore.data
        .map { it.onboarding }

    suspend fun updateGCMParams(gcmParams: GCMParams) {
        dataStore.updateData { it.copy(gcmParams = gcmParams) }
    }

    suspend fun updateOnboarding(onboarding: Boolean) {
        dataStore.updateData { it.copy(onboarding = onboarding) }
    }
}
