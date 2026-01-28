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
import me.mikucat.clementine.app.model.UserData
import me.mikucat.clementine.app.model.UserDataSerializer
import me.mikucat.clementine.app.repo.AppDataRepo
import me.mikucat.clementine.app.repo.AuthAPIRepo
import me.mikucat.clementine.app.repo.KeyStoreRepo
import me.mikucat.clementine.app.repo.PlayAPIRepo
import me.mikucat.clementine.app.repo.UserDataRepo
import me.mikucat.clementine.app.viewmodel.BeanfunLoginViewModel
import me.mikucat.clementine.app.viewmodel.GamaLoginViewModel
import me.mikucat.clementine.app.viewmodel.OnboardingViewModel
import me.mikucat.clementine.app.viewmodel.ScanViewModel
import me.mikucat.clementine.app.viewmodel.SplashViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val userDataName = named("UserData")
private val appDataName = named("AppData")
val ioDispatcher = named("Dispatchers.IO")

val commonModule = module {
    single(ioDispatcher) { Dispatchers.IO }
    single {
        Json {
            ignoreUnknownKeys = true
        }
    }
    single(userDataName) { androidContext().userDataStore }
    single(appDataName) { androidContext().appDataStore }
    single { PlayAPI(LogLevel.INFO) }
    single { UserDataRepo(get(userDataName)) }
    single { AppDataRepo(get(appDataName)) }
    singleOf(::AuthAPIRepo)
    singleOf(::PlayAPIRepo)
    singleOf(::KeyStoreRepo)
    viewModelOf(::SplashViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::GamaLoginViewModel)
    viewModelOf(::ScanViewModel)
    viewModelOf(::BeanfunLoginViewModel)
}

private val Context.appDataStore: DataStore<AppData> by dataStore(
    fileName = "app.json",
    serializer = AppDataSerializer,
)

private val Context.userDataStore: DataStore<UserData> by dataStore(
    fileName = "user.json",
    serializer = UserDataSerializer,
)
