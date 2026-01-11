@file:Suppress("detekt:InjectDispatcher")

package me.mikucat.clementine.app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import io.ktor.client.plugins.logging.LogLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import me.mikucat.clementine.PlayAPI
import me.mikucat.clementine.app.model.AppData
import me.mikucat.clementine.app.model.AppDataSerializer
import me.mikucat.clementine.app.repo.AppDataRepo
import me.mikucat.clementine.app.repo.AuthAPIRepo
import me.mikucat.clementine.app.repo.PlayAPIRepo
import me.mikucat.clementine.app.viewmodel.BeanfunLoginViewModel
import me.mikucat.clementine.app.viewmodel.GamaLoginViewModel
import me.mikucat.clementine.app.viewmodel.ScanViewModel
import me.mikucat.clementine.app.viewmodel.SplashViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val userDataName = named("UserData")
val ioDispatcher = named("Dispatchers.IO")

val commonModule = module {
    single(ioDispatcher) { Dispatchers.IO }
    single {
        Json {
            ignoreUnknownKeys = true
        }
    }
    single(userDataName) { androidContext().appDataStore }
    single { PlayAPI(LogLevel.INFO) }
    single { AppDataRepo(get(userDataName)) }
    singleOf(::AuthAPIRepo)
    singleOf(::PlayAPIRepo)
    viewModelOf(::SplashViewModel)
    viewModelOf(::GamaLoginViewModel)
    viewModelOf(::ScanViewModel)
    viewModelOf(::BeanfunLoginViewModel)
}

private val Context.appDataStore: DataStore<AppData> by dataStore(
    fileName = "data.json",
    serializer = AppDataSerializer,
)
